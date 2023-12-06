package kms.onlinezoostore.repositories;

import kms.onlinezoostore.entities.Token;
import kms.onlinezoostore.entities.enums.TokenPurpose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long>, JpaSpecificationExecutor<Token> {

    List<Token> findAllByRevokedFalse();

    @Query(value = "SELECT t FROM Token t INNER JOIN User u " +
            "ON u.id = :userId AND t.user.id = u.id " +
            "   AND NOT t.revoked ")
    List<Token> findAllValidTokensByUserId(Long userId);

    @Query(value = "SELECT t FROM Token t INNER JOIN User u " +
            "ON u.id = :userId AND t.user.id = u.id " +
            "  AND t.purpose = :purpose " +
            "  AND NOT t.revoked ")
    List<Token> findAllValidTokensByUserIdAndPurpose(Long userId, TokenPurpose purpose);

    Optional<Token> findByTokenValueEqualsAndUserId(String tokenValue, Long userId);
}
