package kms.onlinezoostore.controllers.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Methods for unauthenticated users")
@RequestMapping(value = AuthenticationController.REST_URL)
public class AuthenticationController {
    public static final String REST_URL = "/api/v1/auth";

    private final AuthenticationService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "User registration",
               description = "Register a new user account and send him a confirmation letter")
    public void signup(@RequestBody @Valid UserCreateRequestDto userRequestDto,
                       @NotNull HttpServletRequest httpRequest) {
        authService.signup(userRequestDto,
                    ApplicationUtil.applicationUrl(httpRequest)
        );
    }

    @PostMapping("/verify-email")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Verify user email",
               description = "Verify user email to complete registration using the provided token")
    public void verifyUserEmail(@NotNull @RequestParam("token") String verificationToken) {
        authService.verifyConfirmationLinkFromUser(verificationToken);
    }

    @PostMapping("/resend-verification-link")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Resend verification link",
               description = "Resend the account verification link to the user's email")
    public void resendVerificationLink(@NotNull @RequestParam("email") String email,
                                       @NotNull HttpServletRequest httpRequest) {
        authService.resendAccountVerificationLink(email,
                ApplicationUtil.applicationUrl(httpRequest)
        );
    }

    @PostMapping("/forgot-password")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Forgot password",
               description = "Initiate the process to reset the user's forgotten password")
    public void forgotPassword(@NonNull @RequestParam("email") String email,
                               @NonNull HttpServletRequest httpRequest) {
        authService.forgotPassword(email,
                    ApplicationUtil.applicationUrl(httpRequest)
        );
    }

    @PostMapping("/reset-password")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Reset password",
               description = "Reset the user's password using the provided token")
    public void resetPassword(@NonNull @RequestParam("token") String verificationToken,
                              @RequestBody @Valid ResetPasswordRequestDto request) {
        authService.resetPassword(verificationToken, request);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "User login",
               description = "Authenticate the user and generate A/R tokens")
    public AuthenticationResponse login(@RequestBody AuthenticationRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh-token")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Refresh access token",
               description = "Refresh the user's access token using the refresh token")
    public AuthenticationResponse refreshToken(@NonNull HttpServletRequest request, Principal connectedUser) {
        return authService.refreshToken(request, connectedUser);
    }
}
