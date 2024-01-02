package kms.onlinezoostore.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(name = "ChangePasswordRequest")
public class ChangePasswordRequest {

    @Email(message = "Email is invalid")
    @NotBlank(message = "Email should not be empty")
    private final String email;

    @NotBlank(message = "Current password should not be empty")
    @Size(min = 6, max = 20, message = "Current password should be greater than 6 and less than 20 symbols")
    private final String currentPassword;

    @NotBlank(message = "New password should not be empty")
    @Size(min = 6, max = 20, message = "New password should be greater than 6 and less than 20 symbols")
    private final String newPassword;
}
