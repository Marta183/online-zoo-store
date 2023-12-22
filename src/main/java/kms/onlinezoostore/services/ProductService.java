package kms.onlinezoostore.services;

import kms.onlinezoostore.dto.ProductDto;
import kms.onlinezoostore.services.files.images.MultipleImagesOwnerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.MultiValueMap;

public interface ProductService extends MultipleImagesOwnerService {

    ProductDto findById(Long id);

    Page<ProductDto> findPage(Pageable pageable);
    Page<ProductDto> findPageByMultipleCriteria(MultiValueMap<String, String> params, Pageable pageable);

    Double findMaxProductPrice();

    ProductDto create(ProductDto productDto);
    void update(Long id, ProductDto productDto);
    void deleteById(Long id);
}
