package kms.onlinezoostore.repositories;

import kms.onlinezoostore.entities.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
    List<ProductCategory> findAllByNameLikeIgnoreCase(String nameStartingWith);

    List<ProductCategory> findAllByParent_Id(Long parentId);

    long countAllByParent_IdAndNameIgnoreCase(Long parent_id, String name);
}
