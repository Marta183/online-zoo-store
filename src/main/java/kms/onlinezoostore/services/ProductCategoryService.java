package kms.onlinezoostore.services;

import kms.onlinezoostore.dto.ProductCategoryDto;

import java.util.List;

public interface ProductCategoryService {

    ProductCategoryDto findById(Long id);
    List<ProductCategoryDto> findAll();
    List<ProductCategoryDto> findAllByNameLike(String nameLike);
    List<ProductCategoryDto> findAllByParentId(Long idParentCategory);

    ProductCategoryDto create(ProductCategoryDto category);
    void update(Long id, ProductCategoryDto category);
    void deleteById(Long id);
}
