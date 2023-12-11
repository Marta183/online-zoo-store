package kms.onlinezoostore.security.authentication;

import jakarta.servlet.http.HttpServletRequest;
import kms.onlinezoostore.dto.user.ResetPasswordRequestDto;
import kms.onlinezoostore.dto.user.UserCreateRequestDto;
import java.security.Principal;

public interface AuthenticationService {
    void signup(UserCreateRequestDto userDto, String applicationUrl);

    AuthenticationResponse login(AuthenticationRequest request);

    AuthenticationResponse refreshToken(HttpServletRequest request, Principal connectedUser);

    void verifyConfirmationLinkFromUser(String verificationToken);

    void resendAccountVerificationLink(String email, String applicationUrl);

    void forgotPassword(String email, String applicationUrl);

    void resetPassword(String verificationToken, ResetPasswordRequestDto request);
}
