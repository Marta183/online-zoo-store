package kms.onlinezoostore.security.authentication;

import kms.onlinezoostore.dto.CartDto;
import kms.onlinezoostore.dto.WishListDto;
import kms.onlinezoostore.dto.user.UserResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.RequiredArgsConstructor;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
public class AuthenticationResponse {
    private final String accessToken;
    private final String refreshToken;
    private final UserResponse user;
    private final CartDto cart;
    private final WishListDto wishList;
}
