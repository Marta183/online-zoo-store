package kms.onlinezoostore.security.jwt;

import kms.onlinezoostore.entities.enums.TokenPurpose;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtProvider {
    String extractUsername(String token);

    String generateToken(UserDetails userDetails, TokenPurpose purpose);

    boolean isTokenValid(String token);
}
