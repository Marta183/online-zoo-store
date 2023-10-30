package kms.onlinezoostore.repositories;

import kms.onlinezoostore.entities.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

    List<ProductCategory> findAllByNameContainsIgnoreCase(String nameLike);
    List<ProductCategory> findAllByParentIsNull();

    long countAllByParentIdAndNameIgnoreCase(Long parentId, String name);

    @Query(value = "WITH RECURSIVE categoriesCTE AS ( " +
            "SELECT id " +
            "FROM product_categories " +
            "WHERE id in :parentIds " +
            "UNION ALL " +
            "SELECT inners.id " +
            "FROM categoriesCTE parents, product_categories inners " +
            "WHERE inners.parent_id = parents.id)" +
            "SELECT id FROM categoriesCTE ", nativeQuery = true)
    List<Long> findNestedCategoryIds(@Param("parentIds") List<Long> parentIds);
}
