package kms.onlinezoostore.services.impl;

import kms.onlinezoostore.dto.ProductSizeDto;
import kms.onlinezoostore.dto.mappers.ProductSizeMapper;
import kms.onlinezoostore.entities.ProductSize;
import kms.onlinezoostore.exceptions.EntityNotFoundException;
import kms.onlinezoostore.repositories.ProductSizeRepository;
import kms.onlinezoostore.services.ProductSizeService;
import kms.onlinezoostore.utils.UniqueFieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
    public ProductSizeDto findById(Long id) {
        ProductSizeDto sizeDto = productSizeRepository.findById(id)
                .map(ProductSizeMapper.INSTANCE::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));
        // log
        return sizeDto;
    }

    @Override
    public List<ProductSizeDto> findAll() {
        return productSizeRepository.findAll()
                .stream().map(ProductSizeMapper.INSTANCE::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductSizeDto create(ProductSizeDto productSizeDto) {
        // log
        uniqueFieldService.checkIsFieldValueUniqueOrElseThrow(productSizeRepository, "name", productSizeDto.getName());
        // log
        ProductSize productSize = ProductSizeMapper.INSTANCE.mapToEntity(productSizeDto);
        // log
        ProductSize savedProductSize = productSizeRepository.save(productSize);
        // log
        ProductSizeDto savedProductSizeDto = ProductSizeMapper.INSTANCE.mapToDto(savedProductSize);
        // log
        return savedProductSizeDto;
    }

    @Override
    @Transactional
    public void update(Long id, ProductSizeDto updatedSizeDto) {
        // log
        ProductSize existingSize = productSizeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));
        // log
        if (!existingSize.getName().equals(updatedSizeDto.getName())) {
            uniqueFieldService.checkIsFieldValueUniqueOrElseThrow(productSizeRepository, "name", updatedSizeDto.getName());
        }
        ProductSize updatedSize = ProductSizeMapper.INSTANCE.mapToEntity(updatedSizeDto);
        // log
        updatedSize.setId(id);
        productSizeRepository.save(updatedSize);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        ProductSize size = productSizeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));
        // log
        productSizeRepository.deleteById(id);
    }
}
