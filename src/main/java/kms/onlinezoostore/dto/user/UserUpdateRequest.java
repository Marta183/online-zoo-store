package kms.onlinezoostore.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@Schema(name = "UserUpdateRequest")
public class UserUpdateRequest {
    private final String firstName;
    private final String lastName;
}
