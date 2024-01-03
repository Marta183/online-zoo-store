package kms.onlinezoostore.security.authentication;

import jakarta.servlet.http.HttpServletRequest;
import kms.onlinezoostore.dto.mappers.UserResponseMapper;
import kms.onlinezoostore.dto.user.ResetPasswordRequest;
import kms.onlinezoostore.dto.user.UserCreateRequest;
import kms.onlinezoostore.exceptions.authentication.AccountAlreadyVerifiedException;
import kms.onlinezoostore.entities.User;
import kms.onlinezoostore.exceptions.authentication.InvalidVerificationLink;
import kms.onlinezoostore.exceptions.authentication.VerificationLimitException;
import kms.onlinezoostore.notifications.messages.MessageBuilder;
import kms.onlinezoostore.notifications.messages.MessageType;
import kms.onlinezoostore.security.UserInfoDetails;
import kms.onlinezoostore.security.jwt.JwtProvider;
import kms.onlinezoostore.security.jwt.JwtUtil;
import kms.onlinezoostore.repositories.UserRepository;
import kms.onlinezoostore.notifications.NotificationService;
import kms.onlinezoostore.services.TokenService;
import kms.onlinezoostore.services.UserService;
import kms.onlinezoostore.utils.UsersUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.Map;

import static kms.onlinezoostore.entities.enums.TokenPurpose.*;
import static kms.onlinezoostore.notifications.messages.MessageType.PASSWORD_RESET_CONFIRMATION;
import static kms.onlinezoostore.notifications.messages.MessageType.REGISTRATION_CONFIRMATION;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserDetailsService userDetailsService;
    private final UserResponseMapper userResponseMapper;
    private final UserRepository userRepository;
    private final UserService userService;
    private final TokenService tokenService;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authManager;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;
    private final MessageBuilder messageBuilder;

    @Value("${max-email-verification-attempts}")
    private long maxEmailVerificationAttempts;
    private static final String ENTITY_CLASS_NAME = "USER";

    @Override
    public AuthenticationResponse login(AuthenticationRequest authRequest) {
        log.debug("Login for user {}", authRequest.getEmail());

        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getEmail(),
                        authRequest.getPassword())
        );
        final UserDetails userDetails = (UserDetails) auth.getPrincipal();
        final User user = ((UserInfoDetails) userDetails).getUser();

        tokenService.revokeAllTokensByUser(user);

        String accessToken = tokenService.createToken(user, ACCESS).getTokenValue();
        String refreshToken = authRequest.isRememberMe()
                ? tokenService.createToken(user, REFRESH).getTokenValue()
                : null;

        log.info("User {} logged in successfully", authRequest.getEmail());
        return new AuthenticationResponse(accessToken, refreshToken, userResponseMapper.mapToDto(user));
    }

    @Override
    public void signup(UserCreateRequest request, String applicationUrl) {
        log.debug("{} registration by email {}", ENTITY_CLASS_NAME, request.getEmail());

        User savedUser = userService.createClient(request);
        request = null;

        log.debug("New {} saved in DB with email {}", ENTITY_CLASS_NAME, savedUser.getEmail());

        prepareAndSendConfirmationLink(REGISTRATION_CONFIRMATION, savedUser, applicationUrl);
    }

    @Override
    public void finishRegistrationProcess(String jwt) {
        log.debug("Verify confirmation link with token {}", jwt);

        final User user = findUserByJwt(jwt);
        verifyUserAccountOnRegistrationProcess(user);
        userService.createRelatedEntities(user);

        log.debug("{} with email {} successfully verified after registration", ENTITY_CLASS_NAME, user.getEmail());
    }

    private void verifyUserAccountOnRegistrationProcess(User user) {
        if (user.isEnabled()) {
            log.info("Attempt to verify confirmation link for already verified user {}", user.getEmail());
            throw new AccountAlreadyVerifiedException("This account has already been verified, please, login.");
        }
        user.setEnabled(true);
        user.setConfirmationAttempts(0);
        userRepository.save(user);
    }

    @Override
    public void resendAccountVerificationLink(String email, String applicationUrl) {
        log.debug("Resending account verification link to {} with email {}", ENTITY_CLASS_NAME, email);

        User existingUser = userService.findByEmail(email);

        if (existingUser.isEnabled())
            throw new AccountAlreadyVerifiedException("This account has already been verified, please, login.");
        if (existingUser.getConfirmationAttempts() >= maxEmailVerificationAttempts)
            throw new VerificationLimitException("You reached the limit. Confirmation link cannot be resend for this user.");

        prepareAndSendConfirmationLink(REGISTRATION_CONFIRMATION, existingUser, applicationUrl);

        log.debug("Resent account verification link to {} with email {}", ENTITY_CLASS_NAME, email);
    }

    @Override
    public AuthenticationResponse refreshToken(HttpServletRequest request, Principal connectedUser) {
        log.debug("Refresh token for user {}", connectedUser.getName());

        final String refreshToken = JwtUtil.resolveTokenFromRequest(request);
        final User user = UsersUtil.extractUser(connectedUser);

        if (tokenService.isTokenValid(refreshToken, user)) {
            tokenService.revokeAllAccessTokensByUser(user);
            String newAccessToken = tokenService.createToken(user, ACCESS).getTokenValue();
            log.info("Tokens successfully refreshed for user {}", user.getEmail());
            return new AuthenticationResponse(newAccessToken, refreshToken);
        }

        log.info("Trouble during refreshing tokens for user {}: received refresh token is not valid", user.getEmail());
        throw new BadCredentialsException("Invalid refresh token. Try to login");
    }

    private void prepareAndSendConfirmationLink(MessageType messageType, User user, String applicationUrl) {
        var messageParams = Map.of(
                "verificationLink", createTempVerificationUrlForUser(user, applicationUrl),
                "userEmail", user.getEmail(),
                "username", user.getFirstName()
        );
        notificationService.sendMessage(
                messageBuilder.build(messageType, messageParams)
        );

        user.setConfirmationAttempts(user.getConfirmationAttempts() + 1);
        log.debug("New confirmation attempts for {} with email {} is {}", ENTITY_CLASS_NAME,
                user.getEmail(), user.getConfirmationAttempts());
    }

    private String createTempVerificationUrlForUser(User user, String applicationUrl) {
        String verificationToken = jwtProvider.generateToken(new UserInfoDetails(user), VERIFICATION);
        String verificationUrl = applicationUrl + "?token=" + verificationToken;

        log.debug("Verification url for {} with email {} is {}", ENTITY_CLASS_NAME, user.getEmail(), verificationToken);
        return verificationUrl;
    }

    @Override
    public void forgotPassword(String email, String applicationUrl) {
        log.debug("Forgot password for {} with email {}", ENTITY_CLASS_NAME, email);

        User existingUser = userService.findByEmail(email);
        if (existingUser.getConfirmationAttempts() >= maxEmailVerificationAttempts)
            throw new VerificationLimitException("You reached the limit. Confirmation link cannot be resend for this user.");

        prepareAndSendConfirmationLink(PASSWORD_RESET_CONFIRMATION, existingUser, applicationUrl);
        log.debug("Forgot password confirmation link sent for {} with email {}", ENTITY_CLASS_NAME, email);
    }

    @Override
    public void resetPassword(String jwt, ResetPasswordRequest request) {
        log.debug("Reset password for {} with email {}", ENTITY_CLASS_NAME, request.getEmail());

        final User user = findUserByJwt(jwt);
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setConfirmationAttempts(0);

        log.debug("Password successfully reset for {} with email {}", ENTITY_CLASS_NAME, request.getEmail());
    }

    private User findUserByJwt(String jwt) {
        log.debug("Finding user by token {}", jwt);

        if (!jwtProvider.isTokenValid(jwt)) {
            log.info("Confirmation token is not valid: {}", jwt);
            throw new InvalidVerificationLink();
        }
        final String userEmail = jwtProvider.extractUsername(jwt);
        final UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
        User existingUser = ((UserInfoDetails) userDetails).getUser();

        log.debug("Found user with email {} by token {}", existingUser.getEmail(), jwt);
        return existingUser;
    }
}
