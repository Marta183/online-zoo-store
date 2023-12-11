package kms.onlinezoostore.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.AssertTrue;
import kms.onlinezoostore.entities.enums.UserRole;
import kms.onlinezoostore.entities.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@ToString
@AllArgsConstructor
@Schema(name = "UserCreateRequest")
public class UserCreateRequestDto {

    @NotBlank(message = "First name should not be empty")
    @Size(min = 2, max = 20, message = "First name should be greater than 2 and less than 20 symbols")
    private final String firstName;

    @NotBlank(message = "Last name should not be empty")
    @Size(min = 2, max = 30, message = "Last name should should be greater than 2 and less than 30 symbols")
    private final String lastName;

    @NotNull(message = "Date of birth should not be empty")
    @Past(message = "Date of birth should be in the past")
    private final LocalDate birthDate;

    @Email(message = "Email is invalid")
    @NotBlank(message = "Email should not be empty")
    private final String email;

    @NotBlank(message = "Password should not be empty")
    @Size(min = 6, max = 20, message = "Password should be greater than 6 and less than 20 symbols")
    private final String password;

    @AssertTrue(message = "Consent to process data should be true")
    private final boolean consentToProcessData;

    private final UserRole role;
    private final UserStatus status;
}
