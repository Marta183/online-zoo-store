package kms.onlinezoostore.repositories;

import kms.onlinezoostore.entities.Constant;
import kms.onlinezoostore.entities.enums.ConstantKeys;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConstantRepository extends JpaRepository<Constant, Long>, JpaSpecificationExecutor<Constant> {
    Optional<Constant> findByKey(ConstantKeys key);
}
