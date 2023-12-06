package kms.onlinezoostore.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kms.onlinezoostore.entities.User;
import kms.onlinezoostore.security.jwt.JwtProvider;
import kms.onlinezoostore.security.jwt.JwtUtil;
import kms.onlinezoostore.services.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final UserDetailsService userDetailsService;
    private final TokenService tokenService;
    private final JwtProvider jwtProvider;

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {

        final String jwt = JwtUtil.resolveTokenFromRequest(request);
        if (jwtProvider.isTokenValid(jwt)) {
            final String userEmail = jwtProvider.extractUsername(jwt);
            final UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
            final User user = ((UserInfoDetails) userDetails).getUser();

            tokenService.revokeAllTokensByUser(user);
            SecurityContextHolder.clearContext();
        }
    }
}
