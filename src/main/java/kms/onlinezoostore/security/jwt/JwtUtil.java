package kms.onlinezoostore.security.jwt;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;

public class JwtUtil {
    public static String resolveTokenFromRequest(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.isBlank(authHeader) && StringUtils.startsWith(authHeader, "Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
