package kms.onlinezoostore.services;

import kms.onlinezoostore.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.util.MultiValueMap;

public interface ProductService {

    Product findById(Long id);

    Page<Product> findAllByCategoryId(Long categoryId, Integer pageNumber, Integer pageSize);

    Page<Product> findPageByMultipleCriteria(MultiValueMap<String, String> params, Integer pageNumber, Integer pageSize);

    Product create(Product product);
    Product update(Long id, Product product);

    void deleteById(Long id);
}
