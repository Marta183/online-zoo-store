package kms.onlinezoostore.services;

import kms.onlinezoostore.dto.ProductSizeDto;

import java.util.List;

public interface ProductSizeService {

    ProductSizeDto findById(Long id);
    List<ProductSizeDto> findAll();

    ProductSizeDto create(ProductSizeDto productSizeDto);
    void update(Long id, ProductSizeDto productSizeDto);
    void deleteById(Long id);
}
