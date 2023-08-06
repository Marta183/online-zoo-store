package kms.onlinezoostore.repositories;

import kms.onlinezoostore.entities.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

    List<ProductCategory> findAllByNameContainsIgnoreCase(String nameLike);

    List<ProductCategory> findAllByParentId(Long parentId);

    long countAllByParentIdAndNameIgnoreCase(Long parentId, String name);
}
