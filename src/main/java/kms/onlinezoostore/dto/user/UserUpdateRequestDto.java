package kms.onlinezoostore.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class UserUpdateRequestDto {
    private final String firstName;
    private final String lastName;
}
