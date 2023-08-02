package kms.onlinezoostore.services.impl;

import kms.onlinezoostore.entities.ProductSize;
import kms.onlinezoostore.exceptions.EntityNotFoundException;
import kms.onlinezoostore.repositories.ProductSizeRepository;
import kms.onlinezoostore.services.ProductSizeService;
import kms.onlinezoostore.utils.UniqueFieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductSizeServiceImpl implements ProductSizeService {

    private final ProductSizeRepository productSizeRepository;
    private final UniqueFieldService uniqueFieldService;
    private static final String ENTITY_CLASS_NAME = "ProductSize";

    @Autowired
    public ProductSizeServiceImpl(ProductSizeRepository productSizeRepository, UniqueFieldService uniqueFieldService) {
        this.productSizeRepository = productSizeRepository;
        this.uniqueFieldService = uniqueFieldService;
    }

    @Override
    public ProductSize findById(Long id) {
        ProductSize size = productSizeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));
        // log
        return size;
    }

    @Override
    public List<ProductSize> findAll() {
        return productSizeRepository.findAll();
    }

    @Override
    @Transactional
    public ProductSize create(ProductSize productSize) {
        // log
        uniqueFieldService.checkIsFieldValueUniqueOrElseThrow(productSizeRepository, "name", productSize.getName());
        // log
        ProductSize savedProductSize = productSizeRepository.save(productSize);
        // log
        return savedProductSize;
    }

    @Override
    @Transactional
    public ProductSize update(Long id, ProductSize updatedSize) {
        // log
        ProductSize existingSize = productSizeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));
        // log
        if (!existingSize.getName().equals(updatedSize.getName())) {
            uniqueFieldService.checkIsFieldValueUniqueOrElseThrow(productSizeRepository, "name", updatedSize.getName());
        }
        // log
        updatedSize.setId(id);
        return productSizeRepository.save(updatedSize);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        // log
        productSizeRepository.deleteById(id);
    }
}
