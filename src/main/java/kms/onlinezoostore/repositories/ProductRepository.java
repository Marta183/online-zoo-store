package kms.onlinezoostore.repositories;

import kms.onlinezoostore.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    @Query("SELECT COUNT(p) FROM Product p WHERE p.category.id IN :categoryIds")
    Long countByCategoryIds(@Param("categoryIds") List<Long> categoryIds);

    @Query("SELECT MAX(p.price) FROM Product p")
    Double findMaxPrice();
}
