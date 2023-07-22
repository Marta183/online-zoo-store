package kms.onlinezoostore.services;

import kms.onlinezoostore.entities.Product;
import org.springframework.util.MultiValueMap;

import java.util.List;

public interface ProductService {

    Product findById(Long id);

    List<Product> findAllByCategoryId(Long categoryId);

    List<Product> findByMultipleCriteria(MultiValueMap<String, String> params);

    Product create(Product product);
    Product update(Long id, Product product);

    void deleteById(Long id);
}
