package kms.onlinezoostore.security.authentication;

import jakarta.servlet.http.HttpServletRequest;
import kms.onlinezoostore.dto.user.ResetPasswordRequest;
import kms.onlinezoostore.dto.user.UserCreateRequest;

import java.security.Principal;

public interface AuthenticationService {
    AuthenticationResponse login(AuthenticationRequest request);

    void signup(UserCreateRequest userDto, String applicationUrl);

    void resendAccountVerificationLink(String email, String applicationUrl);

    void finishRegistrationProcess(String verificationToken);

    AuthenticationResponse refreshToken(HttpServletRequest request, Principal connectedUser);

    void forgotPassword(String email, String applicationUrl);

    void resetPassword(String verificationToken, ResetPasswordRequest request);
}
