package kms.onlinezoostore.repositories;

import kms.onlinezoostore.entities.Age;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AgeRepository extends JpaRepository<Age, Long>, JpaSpecificationExecutor<Age> {
}
