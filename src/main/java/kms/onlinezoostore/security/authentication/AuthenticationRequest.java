package kms.onlinezoostore.security.authentication;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthenticationRequest {
    @Email(message = "Email is invalid")
    private final String email;

    @NotBlank(message = "Password should not be empty")
    private final String password;

    private final boolean rememberMe;
}
