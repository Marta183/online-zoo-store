package kms.onlinezoostore.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kms.onlinezoostore.entities.User;
import kms.onlinezoostore.security.jwt.JwtUtil;
import kms.onlinezoostore.services.TokenService;
import kms.onlinezoostore.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final UserService userService;
    private final TokenService tokenService;

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        final String jwt = JwtUtil.resolveTokenFromRequest(request);
        final User user = userService.findByJwt(jwt);
        tokenService.revokeAllTokensByUser(user);
        SecurityContextHolder.clearContext();
    }
}
