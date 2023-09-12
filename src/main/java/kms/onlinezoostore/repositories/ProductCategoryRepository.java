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

    long countAllByParentIdAndNameIgnoreCase(Long parentId, String name);

    @Query("SELECT NEW kms.onlinezoostore.entities.ProductCategory(pc.id, pc.name, pc.parent, COUNT(p.id)) " +
            "FROM ProductCategory pc " +
            "LEFT JOIN pc.products p " +
            "WHERE pc.parent.id = :parentId " +
            "GROUP BY pc.id, pc.name, pc.parent ")
    List<ProductCategory> findInnerCategoriesWithProductCountByParentId(@Param("parentId") Long parentId);
}
