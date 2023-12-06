package kms.onlinezoostore.services.impl;

import kms.onlinezoostore.entities.Token;
import kms.onlinezoostore.entities.User;
import kms.onlinezoostore.entities.enums.TokenPurpose;
import kms.onlinezoostore.entities.enums.TokenType;
import kms.onlinezoostore.repositories.TokenRepository;
import kms.onlinezoostore.security.UserInfoDetails;
import kms.onlinezoostore.security.jwt.JwtProvider;
import kms.onlinezoostore.services.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static kms.onlinezoostore.entities.enums.TokenPurpose.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TokenServiceImpl implements TokenService {
    private final TokenRepository tokenRepository;
    private final JwtProvider jwtProvider;

    private static final String ENTITY_CLASS_NAME = "TOKEN";

    @Override
    public List<Token> findAllValidTokensByUser(User user) {
        log.debug("Finding all valid {} for user with e-mail {}", ENTITY_CLASS_NAME, user.getEmail());

        List<Token> tokens = tokenRepository.findAllValidTokensByUserId(user.getId());

        log.debug("Found {} {} for user with e-mail {}", ENTITY_CLASS_NAME, tokens.size(), user.getEmail());
        return tokens;
    }

    @Override
    public List<Token> findAllValidAccessTokensByUser(User user) {
        log.debug("Finding all valid ACCESS {} for user with e-mail {}", ENTITY_CLASS_NAME, user.getEmail());

        List<Token> tokens = tokenRepository.findAllValidTokensByUserIdAndPurpose(user.getId(), ACCESS);

        log.debug("Found {} ACCESS {} for user with e-mail {}", tokens.size(), ENTITY_CLASS_NAME, user.getEmail());
        return tokens;
    }

    @Override
    public boolean isTokenValid(String jwt, User user) {
        log.debug("Checking if {} {} is valid for user with email {}", ENTITY_CLASS_NAME, jwt, user.getEmail());

        return tokenRepository.findByTokenValueEqualsAndUserId(jwt, user.getId())
                .map(t -> !t.isRevoked())
                .orElse(false);
    }

    @Override
    @Transactional
    public Token createToken(String tokenValue, User user, TokenPurpose purpose) {
        log.debug("Creating new {} {} for user with email {}: {}", purpose, ENTITY_CLASS_NAME, user.getEmail(), tokenValue);

        Token token = Token.builder()
                .tokenValue(tokenValue)
                .tokenType(TokenType.BEARER)
                .user(user)
                .purpose(purpose)
                .revoked(false)
                .build();
        Token savedToken = tokenRepository.save(token);

        log.debug("Created new {} {} for user with email {}: {}", purpose, ENTITY_CLASS_NAME, user.getEmail(), tokenValue);
        return savedToken;
    }

    @Override
    @Transactional
    public Token createToken(User user, TokenPurpose purpose) {
        log.debug("Creating new {} {} for user with email {}", purpose, ENTITY_CLASS_NAME, user.getEmail());

        String jwt = jwtProvider.generateToken(new UserInfoDetails(user), purpose);
        return createToken(jwt, user, purpose);
    }

    @Override
    @Transactional
    public void revokeAllTokensByUser(User user) {
        log.debug("Revoking all {} for user with email {}", ENTITY_CLASS_NAME, user.getEmail());

        List<Token> validUserTokens = findAllValidTokensByUser(user);
        validUserTokens.forEach(token -> token.setRevoked(true));
        tokenRepository.saveAll(validUserTokens);

        log.debug("Revoked all {} for user with email {}", ENTITY_CLASS_NAME, user.getEmail());
    }

    @Override
    @Transactional
    public void revokeAllAccessTokensByUser(User user) {
        log.debug("Revoking all ACCESS {} for user with email {}", ENTITY_CLASS_NAME, user.getEmail());

        List<Token> validUserTokens = findAllValidAccessTokensByUser(user);
        validUserTokens.forEach(token -> token.setRevoked(true));
        tokenRepository.saveAll(validUserTokens);

        log.debug("Revoked all ACCESS {} for user with email {}", ENTITY_CLASS_NAME, user.getEmail());
    }

    @Override
    @Transactional
    public void revokeAllTokens() {
        log.debug("Revoking all {} for all users", ENTITY_CLASS_NAME);

        List<Token> validUserTokens = tokenRepository.findAllByRevokedFalse();
        validUserTokens.forEach(token -> token.setRevoked(true));
        tokenRepository.saveAll(validUserTokens);

        log.debug("Revoked all {} for all users", ENTITY_CLASS_NAME);
    }
}
