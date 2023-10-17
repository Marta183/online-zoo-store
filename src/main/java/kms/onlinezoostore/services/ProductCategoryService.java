package kms.onlinezoostore.services;

import kms.onlinezoostore.dto.ProductCategoryDto;
import kms.onlinezoostore.services.files.images.SingleImageOwnerService;

import java.util.List;

public interface ProductCategoryService extends SingleImageOwnerService {

    ProductCategoryDto findById(Long id);
    List<ProductCategoryDto> findAll();
    List<ProductCategoryDto> findAllMainCategories();
    List<ProductCategoryDto> findAllByNameLike(String nameLike);
    List<ProductCategoryDto> findAllByParentId(Long idParentCategory);

    ProductCategoryDto create(ProductCategoryDto category);
    void update(Long id, ProductCategoryDto category);
    void deleteById(Long id);
}
