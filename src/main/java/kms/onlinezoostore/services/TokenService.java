package kms.onlinezoostore.services;

import kms.onlinezoostore.entities.Token;
import kms.onlinezoostore.entities.User;
import kms.onlinezoostore.entities.enums.TokenPurpose;

import java.util.List;

public interface TokenService {
    List<Token> findAllValidTokensByUser(User user);
    List<Token> findAllValidAccessTokensByUser(User user);

    boolean isTokenValid(String jwt, User user);

    Token createToken(String tokenValue, User user, TokenPurpose purpose);
    Token createToken(User user, TokenPurpose purpose);

    void revokeAllTokensByUser(User user);
    void revokeAllAccessTokensByUser(User user);

    void revokeAllTokens();
}
