package kms.onlinezoostore.security.authentication;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kms.onlinezoostore.entities.User;
import kms.onlinezoostore.security.UserInfoDetails;
import kms.onlinezoostore.security.jwt.JwtProvider;
import kms.onlinezoostore.security.jwt.JwtUtil;
import kms.onlinezoostore.services.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final TokenService tokenService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain
            ) throws ServletException, IOException {

        final String jwt = JwtUtil.resolveTokenFromRequest(request);
        if (jwtProvider.isTokenValid(jwt)) {
            final String userEmail = jwtProvider.extractUsername(jwt);
            final UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
            final User user = ((UserInfoDetails) userDetails).getUser();

            if (tokenService.isTokenValid(jwt, user)
                    && SecurityContextHolder.getContext().getAuthentication() == null
            ) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null,
                        userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
