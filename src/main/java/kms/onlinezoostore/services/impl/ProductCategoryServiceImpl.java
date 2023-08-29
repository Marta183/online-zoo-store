package kms.onlinezoostore.services.impl;

import kms.onlinezoostore.dto.AttachedFileDto;
import kms.onlinezoostore.dto.ProductCategoryDto;
import kms.onlinezoostore.dto.mappers.ProductCategoryMapper;
import kms.onlinezoostore.entities.ProductCategory;
import kms.onlinezoostore.exceptions.EntityDuplicateException;
import kms.onlinezoostore.exceptions.EntityNotFoundException;
import kms.onlinezoostore.repositories.ProductCategoryRepository;
import kms.onlinezoostore.services.ProductCategoryService;
import kms.onlinezoostore.services.files.images.AttachedImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductCategoryServiceImpl implements ProductCategoryService {
    private final ProductCategoryMapper productCategoryMapper;
    private final ProductCategoryRepository categoryRepository;
    private final AttachedImageService attachedImageService;
    private static final String ENTITY_CLASS_NAME = "PRODUCT_CATEGORY";

    @Override
    public ProductCategoryDto findById(Long id) {
        log.debug("Finding {} by ID {}", ENTITY_CLASS_NAME, id);

        ProductCategoryDto categoryDto = categoryRepository.findById(id)
                .map(productCategoryMapper::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));

        log.debug("Found {} by ID {}", ENTITY_CLASS_NAME, id);
        return categoryDto;
    }

    @Override
    public List<ProductCategoryDto> findAll() {
        log.debug("Finding all {}", ENTITY_CLASS_NAME);

        return categoryRepository.findAll()
                .stream().map(productCategoryMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductCategoryDto> findAllByNameLike(String nameLike) {
        log.debug("Finding {} by name like: {}", ENTITY_CLASS_NAME, nameLike);

        return categoryRepository.findAllByNameContainsIgnoreCase(nameLike)
                .stream().map(productCategoryMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductCategoryDto> findAllByParentId(Long idParentCategory) {
        log.debug("Finding {} by parent ID {}", ENTITY_CLASS_NAME, idParentCategory);

        return categoryRepository.findAllByParentId(idParentCategory)
                .stream().map(productCategoryMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductCategoryDto create(ProductCategoryDto categoryDto) {
        log.debug("Creating a new {}: {}", ENTITY_CLASS_NAME, categoryDto.getName());

        checkUniqueNameWithinParentCategory(categoryDto);

        ProductCategory productCategory = productCategoryMapper.mapToEntity(categoryDto);

        ProductCategory savedCategory = categoryRepository.save(productCategory);
        log.debug("New {} saved in DB with ID {}", ENTITY_CLASS_NAME, savedCategory.getId());

        return productCategoryMapper.mapToDto(savedCategory);
    }

    @Override
    @Transactional
    public void update(Long id, ProductCategoryDto updatedCategoryDto) {
        log.debug("Updating {} with ID {}", ENTITY_CLASS_NAME, id);

        ProductCategory existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));

        if (!existingCategory.getName().equals(updatedCategoryDto.getName())) {
            checkUniqueNameWithinParentCategory(updatedCategoryDto);
        }

        ProductCategory updatedCategory = productCategoryMapper.mapToEntity(updatedCategoryDto);
        updatedCategory.setId(id);
        categoryRepository.save(updatedCategory);

        log.debug("{} with ID {} updated in DB", ENTITY_CLASS_NAME, id);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.debug("Deleting {} with ID {}", ENTITY_CLASS_NAME, id);

        ProductCategoryDto existingCategoryDto = categoryRepository.findById(id)
                .map(productCategoryMapper::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));

        // delete entity
        categoryRepository.deleteById(id);

        // delete attached files
        attachedImageService.deleteAllByOwner(existingCategoryDto);

        log.debug("Deleted {} with ID {}", ENTITY_CLASS_NAME, id);
    }

    private void checkUniqueNameWithinParentCategory(ProductCategoryDto categoryDto) {
        String name = categoryDto.getName();
        log.debug("Checking unique name within parent category for {}: {}", ENTITY_CLASS_NAME, name);

        String parentName = null;
        Long parentId = null;
        if (categoryDto.getParent() != null) {
            ProductCategory parentCategory = categoryRepository.findById(categoryDto.getParent().getId()).orElse(null);
            parentName = (parentCategory == null) ? null : parentCategory.getName();
            parentId = (parentCategory == null) ? null : parentCategory.getId();
        }

        log.debug("For {}: {} found parent category with ID {}", ENTITY_CLASS_NAME, name, parentId);

        if (categoryRepository.countAllByParentIdAndNameIgnoreCase(parentId, name) != 0) {
            String message = String.format("Name \'%s\' is already exist in the group \'%s\'", name, parentName);
            throw new EntityDuplicateException(ENTITY_CLASS_NAME, "name", message);
        }
    }
    

    //// IMAGES ////

    @Override
    public AttachedFileDto findImageByOwnerId(Long id) {
        // log
        ProductCategoryDto productCategoryDto = categoryRepository.findById(id)
                .map(productCategoryMapper::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));

        AttachedFileDto attachedFileDto = attachedImageService.findFirstByOwner(productCategoryDto);
        // log
        return attachedFileDto;
    }

    @Override
    @Transactional
    public AttachedFileDto uploadImageByOwnerId(Long id, MultipartFile image) {
        // log
        ProductCategoryDto productCategoryDto = categoryRepository.findById(id)
                .map(productCategoryMapper::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));

        // delete existing image
        AttachedFileDto existingImage = attachedImageService.findFirstByOwner(productCategoryDto);
        if (Objects.nonNull(existingImage)) {
            // log
            attachedImageService.deleteAllByOwner(productCategoryDto);
        }

        // upload new image
        AttachedFileDto uploadedImage = attachedImageService.uploadFileByOwner(productCategoryDto, image);
        // log
        return uploadedImage;
    }

    @Override
    @Transactional
    public void deleteImageByOwnerId(Long id) {
        // log
        ProductCategoryDto productCategoryDto = categoryRepository.findById(id)
                .map(productCategoryMapper::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));

        attachedImageService.deleteAllByOwner(productCategoryDto);
    }
}
