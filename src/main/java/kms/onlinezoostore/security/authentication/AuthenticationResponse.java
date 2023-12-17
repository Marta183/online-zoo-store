package kms.onlinezoostore.security.authentication;

import kms.onlinezoostore.dto.user.UserResponseDto;
import lombok.Getter;
import lombok.Setter;
import lombok.RequiredArgsConstructor;

@Getter
@Setter
@RequiredArgsConstructor
public class AuthenticationResponse {
    private final String accessToken;
    private final String refreshToken;
    private final UserResponseDto userDto;

    public AuthenticationResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userDto = null;
    }
}
