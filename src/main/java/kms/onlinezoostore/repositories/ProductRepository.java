package kms.onlinezoostore.repositories;

import kms.onlinezoostore.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    Page<Product> findAllByCategoryId(Long categoryId, Pageable pageable);
    Page<Product> findAllByBrandId(Long brandId, Pageable pageable);
}
