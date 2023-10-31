package kms.onlinezoostore.services.impl;

import kms.onlinezoostore.dto.AttachedFileDto;
import kms.onlinezoostore.dto.ProductCategoryDto;
import kms.onlinezoostore.dto.mappers.ProductCategoryMapper;
import kms.onlinezoostore.entities.ProductCategory;
import kms.onlinezoostore.exceptions.EntityCannotBeDeleted;
import kms.onlinezoostore.exceptions.EntityDuplicateException;
import kms.onlinezoostore.exceptions.EntityNotFoundException;
import kms.onlinezoostore.repositories.ProductCategoryRepository;
import kms.onlinezoostore.repositories.ProductRepository;
import kms.onlinezoostore.services.ProductCategoryService;
import kms.onlinezoostore.services.files.images.AttachedImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductCategoryServiceImpl implements ProductCategoryService {
    private final ProductCategoryMapper productCategoryMapper;
    private final ProductCategoryRepository categoryRepository;
    private final ProductRepository productRepository;
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
    public List<ProductCategoryDto> findAllMainCategories() {
        log.debug("Finding all main {}", ENTITY_CLASS_NAME);

        return categoryRepository.findAllByParentIsNull()
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

        ProductCategory category = categoryRepository.findById(idParentCategory)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, idParentCategory));

        Set<ProductCategory> innerCategories = category.getInnerCategories();
        log.debug("Found {} {} by parent ID {}", innerCategories.size(), ENTITY_CLASS_NAME, idParentCategory);

        return innerCategories.stream().map(productCategoryMapper::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductCategoryDto create(ProductCategoryDto categoryDto) {
        log.debug("Creating a new {}: {}", ENTITY_CLASS_NAME, categoryDto.getName());

        // check if parent exists
        ProductCategoryDto parentCategoryDto = categoryDto.getParent();
        if (Objects.nonNull(parentCategoryDto)) {
            parentCategoryDto = findById(parentCategoryDto.getId());
        }

        checkUniqueNameWithinParentCategory(categoryDto.getName(), parentCategoryDto);

        // saving
        ProductCategory category = productCategoryMapper.mapToEntity(categoryDto);
        category.setImages(null);
        ProductCategory savedCategory = categoryRepository.save(category);
        log.debug("New {} saved in DB with ID {}", ENTITY_CLASS_NAME, savedCategory.getId());

        return productCategoryMapper.mapToDto(savedCategory);
    }

    @Override
    @Transactional
    public void update(Long id, ProductCategoryDto updatedCategoryDto) {
        log.debug("Updating {} with ID {}", ENTITY_CLASS_NAME, id);

        ProductCategory existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));

        // check if parent exists
        ProductCategoryDto parentCategoryDto = updatedCategoryDto.getParent();
        if (Objects.nonNull(parentCategoryDto)) {
            parentCategoryDto = findById(parentCategoryDto.getId());
        }

        if (!existingCategory.getName().equals(updatedCategoryDto.getName())) {
            checkUniqueNameWithinParentCategory(updatedCategoryDto.getName(), parentCategoryDto);
        }

        ProductCategory updatedCategory = productCategoryMapper.mapToEntity(updatedCategoryDto);
        updatedCategory.setId(id);
        updatedCategory.setImages(existingCategory.getImages());
        categoryRepository.save(updatedCategory);

        log.debug("{} with ID {} updated in DB", ENTITY_CLASS_NAME, id);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.debug("Deleting {} with ID {}", ENTITY_CLASS_NAME, id);

        ProductCategory existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));

        // category can't be deleted if exist products with current category or any of inner categories
        verifyNoProductsInCategoryAndInnersOrThrowException(id);

        // delete entity
        categoryRepository.deleteById(id);
        log.debug("Deleted {} with ID {}", ENTITY_CLASS_NAME, id);

        // delete attached files
        attachedImageService.deleteAllByOwner(productCategoryMapper.mapToDto(existingCategory));
        log.debug("Deleted image for {} with ID {}", ENTITY_CLASS_NAME, id);
    }

    private void verifyNoProductsInCategoryAndInnersOrThrowException(Long id) {
        List<Long> categoryIds = categoryRepository.findNestedCategoryIds(Collections.singletonList(id));
        Long numberOfProducts = productRepository.countByCategoryIds(categoryIds);

        if (numberOfProducts > 0)
            throw new EntityCannotBeDeleted("Cannot delete category by id " + id +
                    ": there are " + numberOfProducts + " product bounded to the category or it's inner categories");
    }

    private void checkUniqueNameWithinParentCategory(String name, ProductCategoryDto parentCategoryDto) {
        log.debug("Checking unique name within parent category for {}: {}", ENTITY_CLASS_NAME, name);

        Long parentId = (parentCategoryDto == null) ? null : parentCategoryDto.getId();
        String parentName = (parentCategoryDto == null) ? null : parentCategoryDto.getName();

        if (categoryRepository.countAllByParentIdAndNameIgnoreCase(parentId, name) != 0) {
            String message = String.format("Name '%s' is already exist in the group '%s'", name, parentName);
            throw new EntityDuplicateException(ENTITY_CLASS_NAME, "name", message);
        }
    }
    

    //// IMAGES ////


    @Override
    @Transactional
    public AttachedFileDto uploadImageByOwnerId(Long id, MultipartFile image) {
        log.debug("Uploading image for {} ID {}", ENTITY_CLASS_NAME, id);

        ProductCategoryDto productCategoryDto = categoryRepository.findById(id)
                .map(productCategoryMapper::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));

        AttachedFileDto uploadedImage = attachedImageService.replaceFileByOwner(productCategoryDto, image);

        log.debug("Uploaded new image with ID {} for {} ID {}", uploadedImage.getId(), ENTITY_CLASS_NAME, id);

        return uploadedImage;
    }

    @Override
    @Transactional
    public void deleteImageByOwnerId(Long id) {
        log.debug("Deleting all images for {} ID {}", ENTITY_CLASS_NAME, id);

        ProductCategoryDto productCategoryDto = categoryRepository.findById(id)
                .map(productCategoryMapper::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));

        attachedImageService.deleteAllByOwner(productCategoryDto);
        log.debug("Deleted all images for {} ID {}", ENTITY_CLASS_NAME, id);
    }
}
