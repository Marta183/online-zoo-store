package kms.onlinezoostore.services;

import kms.onlinezoostore.entities.ProductCategory;

import java.util.List;

public interface ProductCategoryService {

    ProductCategory findById(Long id);
    List<ProductCategory> findAll();
    List<ProductCategory> findAllByNameStartingWith(String nameStartingWith);
    List<ProductCategory> findAllByParentId(String idParentCategory);

    ProductCategory create(ProductCategory category);
    ProductCategory update(Long id, ProductCategory category);
    void deleteById(Long id);
}
