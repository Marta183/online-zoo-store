package kms.onlinezoostore.services.impl;

import kms.onlinezoostore.dto.ProductCategoryDto;
import kms.onlinezoostore.dto.mappers.ProductCategoryMapper;
import kms.onlinezoostore.entities.ProductCategory;
import kms.onlinezoostore.exceptions.EntityDuplicateException;
import kms.onlinezoostore.exceptions.EntityNotFoundException;
import kms.onlinezoostore.repositories.ProductCategoryRepository;
import kms.onlinezoostore.services.ProductCategoryService;
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
public class ProductCategoryServiceImpl implements ProductCategoryService {

    private final ProductCategoryRepository categoryRep;
    private static final String ENTITY_CLASS_NAME = "PRODUCT_CATEGORY";

    @Override
    public ProductCategoryDto findById(Long id) {
        log.debug("Finding {} by ID {}", ENTITY_CLASS_NAME, id);

        ProductCategoryDto categoryDto = categoryRep.findById(id)
                .map(ProductCategoryMapper.INSTANCE::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));

        log.debug("Found {} by ID {}", ENTITY_CLASS_NAME, id);
        return categoryDto;
    }

    @Override
    public List<ProductCategoryDto> findAll() {
        log.debug("Finding all {}", ENTITY_CLASS_NAME);

        return categoryRep.findAll()
                .stream().map(ProductCategoryMapper.INSTANCE::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductCategoryDto> findAllByNameLike(String nameLike) {
        log.debug("Finding {} by name like: {}", ENTITY_CLASS_NAME, nameLike);

        return categoryRep.findAllByNameContainsIgnoreCase(nameLike)
                .stream().map(ProductCategoryMapper.INSTANCE::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductCategoryDto> findAllByParentId(Long idParentCategory) {
        log.debug("Finding {} by parent ID {}", ENTITY_CLASS_NAME, idParentCategory);

        return categoryRep.findAllByParentId(idParentCategory)
                .stream().map(ProductCategoryMapper.INSTANCE::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductCategoryDto create(ProductCategoryDto categoryDto) {
        log.debug("Creating a new {}: {}", ENTITY_CLASS_NAME, categoryDto.getName());

        checkUniqueNameWithinParentCategory(categoryDto);

        ProductCategory productCategory = ProductCategoryMapper.INSTANCE.mapToEntity(categoryDto);

        ProductCategory savedCategory = categoryRep.save(productCategory);
        log.debug("New {} saved in DB with ID {}", ENTITY_CLASS_NAME, savedCategory.getId());

        return ProductCategoryMapper.INSTANCE.mapToDto(savedCategory);
    }

    @Override
    @Transactional
    public void update(Long id, ProductCategoryDto updatedCategoryDto) {
        log.debug("Updating {} with ID {}", ENTITY_CLASS_NAME, id);

        ProductCategory existingCategory = categoryRep.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));

        if (!existingCategory.getName().equals(updatedCategoryDto.getName())) {
            checkUniqueNameWithinParentCategory(updatedCategoryDto);
        }

        ProductCategory updatedCategory = ProductCategoryMapper.INSTANCE.mapToEntity(updatedCategoryDto);
        updatedCategory.setId(id);
        categoryRep.save(updatedCategory);

        log.debug("{} with ID {} updated in DB", ENTITY_CLASS_NAME, id);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.debug("Deleting {} with ID {}", ENTITY_CLASS_NAME, id);

        categoryRep.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));

        categoryRep.deleteById(id);
        log.debug("Deleted {} with ID {}", ENTITY_CLASS_NAME, id);
    }

    private void checkUniqueNameWithinParentCategory(ProductCategoryDto categoryDto) {
        String name = categoryDto.getName();
        log.debug("Checking unique name within parent category for {}: {}", ENTITY_CLASS_NAME, name);

        String parentName = null;
        Long parentId = null;
        if (categoryDto.getParent() != null) {
            ProductCategory parentCategory = categoryRep.findById(categoryDto.getParent().getId()).orElse(null);
            parentName = (parentCategory == null) ? null : parentCategory.getName();
            parentId = (parentCategory == null) ? null : parentCategory.getId();
        }

        log.debug("For {}: {} found parent category with ID {}", ENTITY_CLASS_NAME, name, parentId);

        if (categoryRep.countAllByParentIdAndNameIgnoreCase(parentId, name) != 0) {
            String message = String.format("Name \'%s\' is already exist in the group \'%s\'", name, parentName);
            throw new EntityDuplicateException(ENTITY_CLASS_NAME, "name", message);
        }
    }
}
