package kms.onlinezoostore.security.authentication;

import lombok.Getter;
import lombok.Setter;
import lombok.RequiredArgsConstructor;

@Getter
@Setter
@RequiredArgsConstructor
public class AuthenticationResponse {
    private final String accessToken;
    private final String refreshToken;
}
