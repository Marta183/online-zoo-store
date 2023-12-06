package kms.onlinezoostore.controllers.v1;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kms.onlinezoostore.dto.user.ResetPasswordRequestDto;
import kms.onlinezoostore.dto.user.UserCreateRequestDto;
import kms.onlinezoostore.security.authentication.AuthenticationRequest;
import kms.onlinezoostore.security.authentication.AuthenticationResponse;
import kms.onlinezoostore.security.authentication.AuthenticationService;
import kms.onlinezoostore.utils.ApplicationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = AuthenticationController.REST_URL)
public class AuthenticationController {
    public static final String REST_URL = "/api/v1/auth";

    private final AuthenticationService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public String signup(@RequestBody @Valid UserCreateRequestDto userRequestDto,
                         @NotNull HttpServletRequest httpRequest) {
        return authService.signup(userRequestDto,
                    ApplicationUtil.applicationUrl(httpRequest)
        );
    }

    @GetMapping("/verify-email")
    @ResponseStatus(HttpStatus.OK)
    public void verifyUserEmail(@NotNull @RequestParam("token") String verificationToken) {
        authService.verifyConfirmationLinkFromUser(verificationToken);
    }

    @PostMapping("/resend-verification-link")
    @ResponseStatus(HttpStatus.OK)
    public void resendVerificationLink(@NotNull @RequestParam("email") String email,
                                       @NotNull HttpServletRequest httpRequest) {
        authService.resendAccountVerificationLink(email,
                ApplicationUtil.applicationUrl(httpRequest)
        );
    }

    @PostMapping("/forgot-password")
    @ResponseStatus(HttpStatus.OK)
    public String forgotPassword(@NonNull @RequestParam("email") String email,
                                 @NonNull HttpServletRequest httpRequest) {
        return authService.forgotPassword(email,
                    ApplicationUtil.applicationUrl(httpRequest)
        );
    }

    @PostMapping("/reset-password")
    @ResponseStatus(HttpStatus.OK)
    public void resetPassword(@NonNull @RequestParam("token") String verificationToken,
                              @RequestBody @Valid ResetPasswordRequestDto request) {
        authService.resetPassword(verificationToken, request);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public AuthenticationResponse login(@RequestBody AuthenticationRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh-token")
    @ResponseStatus(HttpStatus.OK)
    public AuthenticationResponse refreshToken(@NonNull HttpServletRequest request, Principal connectedUser) {
        return authService.refreshToken(request, connectedUser);
    }
}
