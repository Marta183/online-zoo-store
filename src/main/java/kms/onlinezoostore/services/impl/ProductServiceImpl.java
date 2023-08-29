package kms.onlinezoostore.services.impl;

import kms.onlinezoostore.dto.AttachedFileDto;
import kms.onlinezoostore.dto.ProductDto;
import kms.onlinezoostore.dto.mappers.AttachedFileMapper;
import kms.onlinezoostore.dto.mappers.ProductMapper;
import kms.onlinezoostore.entities.AttachedFile;
import kms.onlinezoostore.entities.Product;
import kms.onlinezoostore.exceptions.EntityNotFoundException;
import kms.onlinezoostore.repositories.ProductRepository;
import kms.onlinezoostore.repositories.specifications.ProductSpecifications;
import kms.onlinezoostore.services.ProductService;
import kms.onlinezoostore.services.files.images.AttachedImageService;
import kms.onlinezoostore.utils.UniqueFieldService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {
    private final ProductMapper productMapper;
    private final ProductRepository productRep;
    private final AttachedFileMapper attachedFileMapper;
    private final AttachedImageService attachedImageService;
    private final UniqueFieldService uniqueFieldService;

    private static final String ENTITY_CLASS_NAME = "PRODUCT";

    @Override
    public ProductDto findById(Long id) {
        log.debug("Finding {} by ID {}", ENTITY_CLASS_NAME, id);

        ProductDto productDto = productRep.findById(id)
                .map(productMapper::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));

        log.debug("Found {} by ID {}", ENTITY_CLASS_NAME, id);
        return productDto;
    }

    @Override
    public Page<ProductDto> findPage(Pageable pageable) {
        log.debug("Finding {} product page ", ENTITY_CLASS_NAME);
        log.debug("  with page number: {}, page size: {}", pageable.getPageNumber(), pageable.getPageSize());

        Page<ProductDto> page =  productRep.findAll(pageable)
                .map(productMapper::mapToDto);

        log.debug("Found {} product page with number of products: {}", ENTITY_CLASS_NAME, page.getContent().size());
        return page;
    }

    @Override
    public Page<ProductDto> findPageByCategoryId(Long categoryId, Pageable pageable) {
        log.debug("Finding {} product page by category ID {}", ENTITY_CLASS_NAME, categoryId);
        log.debug("  with page number: {}, page size: {}", pageable.getPageNumber(), pageable.getPageSize());

        Page<ProductDto> page =  productRep.findAllByCategoryId(categoryId, pageable)
                .map(productMapper::mapToDto);

        log.debug("Found {} product page with number of products: {}", ENTITY_CLASS_NAME, page.getContent().size());
        return page;
    }

    @Override
    public Page<ProductDto> findPageByBrandId(Long brandId, Pageable pageable) {
        log.debug("Finding {} product page by brand ID {}", ENTITY_CLASS_NAME, brandId);
        log.debug("  with page number: {}, page size: {}", pageable.getPageNumber(), pageable.getPageSize());

        Page<ProductDto> page =  productRep.findAllByBrandId(brandId, pageable)
                .map(productMapper::mapToDto);

        log.debug("Found {} product page with number of products: {}", ENTITY_CLASS_NAME, page.getContent().size());
        return page;
    }

    @Override
    public Page<ProductDto> findPageByMultipleCriteria(MultiValueMap<String, String> params, Pageable pageable) {
        log.debug("Finding {} page by multiple criteria", ENTITY_CLASS_NAME);
        log.debug("  with page number: {}, page size: {}", pageable.getPageNumber(), pageable.getPageSize());

        processParamsForCriteriaBuilder(params);

        Page<ProductDto> page = productRep.findAll(ProductSpecifications.build(params), pageable)
                .map(productMapper::mapToDto);

        log.debug("Found {} page with number of products: {}", ENTITY_CLASS_NAME, page.getContent().size());
        return page;
    }

    @Override
    @Transactional
    public ProductDto create(ProductDto productDto) {
        log.debug("Creating a new {}: {}", ENTITY_CLASS_NAME, productDto.getName());

        uniqueFieldService.checkIsFieldValueUniqueOrElseThrow(productRep, "name", productDto.getName());

        // saving entity
        Product product = productMapper.mapToEntity(productDto);
        product.setMainImage(null);

        Product savedProduct = productRep.save(product);
        log.debug("New {} saved in DB with ID {}", ENTITY_CLASS_NAME, savedProduct.getId());

        return productMapper.mapToDto(savedProduct);
    }

    @Override
    @Transactional
    public void update(Long id, ProductDto updatedProductDto) {
        log.debug("Updating {} with ID {}", ENTITY_CLASS_NAME, id);

        Product existingProduct = productRep.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));

        // check unique name
        if (!existingProduct.getName().equals(updatedProductDto.getName())) {
            uniqueFieldService.checkIsFieldValueUniqueOrElseThrow(productRep, "name", updatedProductDto.getName());
        }

        // control main image
        AttachedFile newMainImage = attachedFileMapper.mapToEntity(updatedProductDto.getMainImage());
        AttachedFile oldMainImage = existingProduct.getMainImage();
        if (!newMainImage.equals(oldMainImage)) {
            // log
            // make sure that new mainImage's owner is current product
            attachedImageService.findByIdAndOwner(newMainImage.getId(), productMapper.mapToDto(existingProduct)); // attachedImageService.findByFilePathAndOwner(newMainImagePath, updatedProductDto);
        }

        // saving entity
        Product productToUpdate = productMapper.mapToEntity(updatedProductDto);
        productToUpdate.setId(id);
        productToUpdate.setCreatedAt(existingProduct.getCreatedAt());
        productRep.save(productToUpdate);

        log.debug("{} with ID {} updated in DB", ENTITY_CLASS_NAME, id);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.debug("Deleting {} with ID {}", ENTITY_CLASS_NAME, id);

        ProductDto existingProductDto = productRep.findById(id).map(productMapper::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));

        // delete entity
        productRep.deleteById(id);

        // delete attached files
        attachedImageService.deleteAllByOwner(existingProductDto);

        log.debug("Deleted {} with ID {}", ENTITY_CLASS_NAME, id);
    }

    private void processParamsForCriteriaBuilder(MultiValueMap<String, String> params) {
        log.debug("Processing {} params for criteria builder", ENTITY_CLASS_NAME);
        deleteInvalidParams(params);

        if (params.containsKey("minPrice")
                && params.getFirst("minPrice") != null
                && Double.parseDouble(params.getFirst("minPrice")) == 0) {
            log.debug("Removing minPrice param with value 0");
            params.remove("minPrice");
        }
    }

    private void deleteInvalidParams(MultiValueMap<String, String> source) {
        log.debug("Deleting invalid {} params for criteria builder", ENTITY_CLASS_NAME);

        Iterator<Map.Entry<String, List<String>>> mapIterator = source.entrySet().iterator();
        while (mapIterator.hasNext()) {
            Map.Entry<String, List<String>> entry = mapIterator.next();

            entry.getValue().removeIf(str -> str == null || str.isEmpty()); // remove particular value

            if (entry.getValue().isEmpty()) {
                log.debug("Removing empty {} param for criteria builder: {}", ENTITY_CLASS_NAME, entry.getKey());
                mapIterator.remove(); // remove map.entry
            }
        }
    }


    //// IMAGES ////

    @Override
    public Set<AttachedFileDto> findAllImagesByOwnerId(Long productId) {
        // log
        ProductDto productDto = productRep.findById(productId)
                .map(productMapper::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, productId));

        Set<AttachedFileDto>  attachedFilesDto = attachedImageService.findAllByOwner(productDto);
        // log
        return attachedFilesDto;
    }

    @Override
    public AttachedFileDto findImageByIdAndOwnerId(Long productId, Long imageId) {
        // log
        ProductDto productDto = productRep.findById(productId)
                .map(productMapper::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, productId));

        AttachedFileDto attachedFileDto = attachedImageService.findByIdAndOwner(imageId, productDto);
        // log
        return attachedFileDto;
    }

    @Override
    @Transactional
    public Set<AttachedFileDto> uploadImagesByOwnerId(Long productId, List<MultipartFile> images) {
        // log
        ProductDto productDto = productRep.findById(productId)
                .map(productMapper::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, productId));

        Set<AttachedFileDto> uploadedImages = attachedImageService.uploadFilesByOwner(productDto, images);
        // log
        return uploadedImages;
    }

    @Override
    @Transactional
    public void deleteAllImagesByOwnerId(Long productId) {
        // log
        Product product = productRep.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, productId));

        product.setMainImage(null);

        attachedImageService.deleteAllByOwner(productMapper.mapToDto(product));
    }

    @Override
    @Transactional
    public void deleteImageByIdAndOwnerId(Long productId, Long imageId) {
        // log
        Product product = productRep.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, productId));

        if (Objects.nonNull(product.getMainImage()) && product.getMainImage().getId() == imageId) {
            product.setMainImage(null);
        }
        attachedImageService.deleteByIdAndOwner(imageId, productMapper.mapToDto(product));
    }
}
