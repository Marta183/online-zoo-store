package kms.onlinezoostore.services;

import kms.onlinezoostore.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.MultiValueMap;

public interface ProductService {

    Product findById(Long id);

    Page<Product> findPageByCategoryId(Long categoryId, Pageable pageable);

    Page<Product> findPageByMultipleCriteria(MultiValueMap<String, String> params, Pageable pageable);

    Product create(Product product);
    Product update(Long id, Product product);

    void deleteById(Long id);
}
