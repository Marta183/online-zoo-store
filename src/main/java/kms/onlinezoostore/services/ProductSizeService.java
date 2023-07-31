package kms.onlinezoostore.services;


import kms.onlinezoostore.entities.ProductSize;

import java.util.List;

public interface ProductSizeService {

    ProductSize findById(Long id);
    List<ProductSize> findAll();

    ProductSize create(ProductSize productSize);
    ProductSize update(Long id, ProductSize productSize);
    void deleteById(Long id);
}
