package kms.onlinezoostore.services;

import kms.onlinezoostore.dto.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.MultiValueMap;

public interface ProductService {

    ProductDto findById(Long id);

    Page<ProductDto> findPage(Pageable pageable);
    Page<ProductDto> findPageByCategoryId(Long categoryId, Pageable pageable);
    Page<ProductDto> findPageByBrandId(Long brandId, Pageable pageable);
    Page<ProductDto> findPageByMultipleCriteria(MultiValueMap<String, String> params, Pageable pageable);

    ProductDto create(ProductDto productDto);
    void update(Long id, ProductDto productDto);
    void deleteById(Long id);
}
