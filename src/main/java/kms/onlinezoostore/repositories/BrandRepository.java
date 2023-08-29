package kms.onlinezoostore.repositories;

import kms.onlinezoostore.entities.Brand;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long>, JpaSpecificationExecutor<Brand> {

//    @Query(nativeQuery = true, value = "" +
//            "WITH brand_with_image_id;" +
//            "SELECT b.*, MAX(f.id) image_id" +
//            "FROM brands AS b" +
//            "    LEFT JOIN attached_files AS f" +
//            "    ON b.id = f.owner_id" +
//            "GROUP BY b.id" +
//            ";" +
//            "SELECT b.*, f.id AS image_id, f.file_path AS image_url" +
//            "FROM brand_with_image_id AS b" +
//            "   LEFT JOIN attached_files AS f" +
//            "   ON b.id = f.owner_id" +
//            "       AND b.image_id = f.id")
//    Optional<Brand> findById(Specification<Brand> spec);
}
