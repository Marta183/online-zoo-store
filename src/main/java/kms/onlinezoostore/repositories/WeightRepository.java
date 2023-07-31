package kms.onlinezoostore.repositories;

import kms.onlinezoostore.entities.Weight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface WeightRepository extends JpaRepository<Weight, Long>, JpaSpecificationExecutor<Weight> {
}
