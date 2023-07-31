package kms.onlinezoostore.services.impl;

import kms.onlinezoostore.entities.ProductSize;
import kms.onlinezoostore.repositories.ProductSizeRepository;
import kms.onlinezoostore.services.ProductSizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductSizeServiceImpl implements ProductSizeService {

    private final ProductSizeRepository productSizeRepository;
    @Autowired
    public ProductSizeServiceImpl(ProductSizeRepository productSizeRepository) {
        this.productSizeRepository = productSizeRepository;
    }

    @Override
    public ProductSize findById(Long id) {
        return productSizeRepository.findById(id).orElse(null);
    }

    @Override
    public List<ProductSize> findAll() {
        return productSizeRepository.findAll();
    }

    @Override
    @Transactional
    public ProductSize create(ProductSize productSize) {
        return productSizeRepository.save(productSize);
    }

    @Override
    @Transactional
    public ProductSize update(Long id, ProductSize updatedSize) {
        updatedSize.setId(id);
        return productSizeRepository.save(updatedSize);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        productSizeRepository.deleteById(id);
    }
}
