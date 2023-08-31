package kms.onlinezoostore.services.impl;

import kms.onlinezoostore.dto.ProductSizeDto;
import kms.onlinezoostore.dto.mappers.ProductSizeMapper;
import kms.onlinezoostore.entities.ProductSize;
import kms.onlinezoostore.exceptions.EntityNotFoundException;
import kms.onlinezoostore.repositories.ProductSizeRepository;
import kms.onlinezoostore.services.ProductSizeService;
import kms.onlinezoostore.utils.UniqueFieldService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductSizeServiceImpl implements ProductSizeService {
    private final ProductSizeMapper productSizeMapper;

    private final ProductSizeRepository productSizeRepository;
    private final UniqueFieldService uniqueFieldService;
    private static final String ENTITY_CLASS_NAME = "PRODUCT_SIZE";

    @Override
    public ProductSizeDto findById(Long id) {
        log.debug("Finding {} by ID {}", ENTITY_CLASS_NAME, id);

        ProductSizeDto sizeDto = productSizeRepository.findById(id).map(productSizeMapper::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));

        log.debug("Found {} by ID {}", ENTITY_CLASS_NAME, id);
        return sizeDto;
    }

    @Override
    public List<ProductSizeDto> findAll() {
        log.debug("Finding all {}", ENTITY_CLASS_NAME);

        return productSizeRepository.findAll()
                .stream().map(productSizeMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductSizeDto create(ProductSizeDto productSizeDto) {
        log.debug("Creating a new {}: {}", ENTITY_CLASS_NAME, productSizeDto.getName());

        uniqueFieldService.checkIsFieldValueUniqueOrElseThrow(productSizeRepository, "name", productSizeDto.getName());

        ProductSize productSize = productSizeMapper.mapToEntity(productSizeDto);

        ProductSize savedProductSize = productSizeRepository.save(productSize);
        log.debug("New {} saved in DB with ID {}", ENTITY_CLASS_NAME, savedProductSize.getId());

        return productSizeMapper.mapToDto(savedProductSize);
    }

    @Override
    @Transactional
    public void update(Long id, ProductSizeDto updatedSizeDto) {
        log.debug("Updating {} with ID {}", ENTITY_CLASS_NAME, id);

        ProductSize existingSize = productSizeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));

        if (!existingSize.getName().equals(updatedSizeDto.getName())) {
            uniqueFieldService.checkIsFieldValueUniqueOrElseThrow(productSizeRepository, "name", updatedSizeDto.getName());
        }

        ProductSize updatedSize = productSizeMapper.mapToEntity(updatedSizeDto);
        updatedSize.setId(id);
        productSizeRepository.save(updatedSize);

        log.debug("{} with ID {} updated in DB", ENTITY_CLASS_NAME, id);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.debug("Deleting {} with ID {}", ENTITY_CLASS_NAME, id);

        productSizeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));

        productSizeRepository.deleteById(id);
        log.debug("Deleted {} with ID {}", ENTITY_CLASS_NAME, id);
    }
}
