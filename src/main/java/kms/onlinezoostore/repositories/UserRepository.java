package kms.onlinezoostore.repositories;

import kms.onlinezoostore.entities.User;
import kms.onlinezoostore.entities.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmailIgnoreCase(String email);

    Page<User> findAllByEnabledTrue(Pageable pageable);
    Page<User> findAllByRoleAndEnabledTrue(UserRole role, Pageable pageable);
}
