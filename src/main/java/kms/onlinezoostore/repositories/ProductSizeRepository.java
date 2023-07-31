package kms.onlinezoostore.repositories;

import kms.onlinezoostore.entities.ProductSize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductSizeRepository extends JpaRepository<ProductSize, Long>, JpaSpecificationExecutor<ProductSize> {
}
