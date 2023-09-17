package kms.onlinezoostore.services.impl;

import kms.onlinezoostore.dto.AttachedFileDto;
import kms.onlinezoostore.dto.ProductDto;
import kms.onlinezoostore.dto.mappers.AttachedFileMapper;
import kms.onlinezoostore.dto.mappers.ProductMapper;
import kms.onlinezoostore.entities.AttachedFile;
import kms.onlinezoostore.entities.Product;
import kms.onlinezoostore.exceptions.EntityNotFoundException;
import kms.onlinezoostore.exceptions.PriceConflictException;
import kms.onlinezoostore.repositories.ProductRepository;
import kms.onlinezoostore.repositories.specifications.ProductSpecifications;
import kms.onlinezoostore.services.*;
import kms.onlinezoostore.services.files.images.AttachedImageService;
import kms.onlinezoostore.utils.UniqueFieldService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindException;
import org.springframework.web.multipart.MultipartFile;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {
    private final ProductMapper productMapper;
    private final ProductRepository productRep;
    private final AttachedFileMapper attachedFileMapper;

    private final UniqueFieldService uniqueFieldService;

    private final AttachedImageService attachedImageService;
    private final AgeService ageService;
    private final BrandService brandService;
    private final ColorService colorService;
    private final MaterialService materialService;
    private final PrescriptionService prescriptionService;
    private final ProductCategoryService productCategoryService;
    private final ProductSizeService productSizeService;
    private final WeightService weightService;

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

        Page<ProductDto> page = productRep.findAll(pageable)
                .map(productMapper::mapToDto);

        log.debug("Found {} product page with number of products: {}", ENTITY_CLASS_NAME, page.getContent().size());
        return page;
    }

    @Override
    public Page<ProductDto> findPageByCategoryId(Long categoryId, Pageable pageable) {
        log.debug("Finding {} product page by category ID {}", ENTITY_CLASS_NAME, categoryId);
        log.debug("  with page number: {}, page size: {}", pageable.getPageNumber(), pageable.getPageSize());

        Page<ProductDto> page = productRep.findAllByCategoryId(categoryId, pageable)
                .map(productMapper::mapToDto);

        log.debug("Found {} product page with number of products: {}", ENTITY_CLASS_NAME, page.getContent().size());
        return page;
    }

    @Override
    public Page<ProductDto> findPageByBrandId(Long brandId, Pageable pageable) {
        log.debug("Finding {} product page by brand ID {}", ENTITY_CLASS_NAME, brandId);
        log.debug("  with page number: {}, page size: {}", pageable.getPageNumber(), pageable.getPageSize());

        Page<ProductDto> page = productRep.findAllByBrandId(brandId, pageable)
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

        findDependentEntitiesByForeignKeyOrThrowException(productDto);

        verifyPrices(productDto);

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

        findDependentEntitiesByForeignKeyOrThrowException(updatedProductDto);

        verifyPrices(updatedProductDto);

        // check unique name
        if (!existingProduct.getName().equals(updatedProductDto.getName())) {
            uniqueFieldService.checkIsFieldValueUniqueOrElseThrow(productRep, "name", updatedProductDto.getName());
        }

        // control main image
        AttachedFile newMainImage = attachedFileMapper.mapToEntity(updatedProductDto.getMainImage());
        AttachedFile oldMainImage = existingProduct.getMainImage();
        if (Objects.nonNull(newMainImage) && !newMainImage.equals(oldMainImage)) {
            log.debug("Main image has changed for {} with ID {}: new image ID {}", ENTITY_CLASS_NAME, id, newMainImage.getId());
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
        log.debug("Deleted {} with ID {}", ENTITY_CLASS_NAME, id);

        // delete attached files
        attachedImageService.deleteAllByOwner(existingProductDto);
        log.debug("Deleted images for {} with ID {}", ENTITY_CLASS_NAME, id);
    }

    private void processParamsForCriteriaBuilder(MultiValueMap<String, String> params) {
        log.debug("Processing {} params for criteria builder", ENTITY_CLASS_NAME);

        deleteInvalidParams(params);

        if (params.containsKey("minPrice")
                && Objects.nonNull(params.getFirst("minPrice"))
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

    private void findDependentEntitiesByForeignKeyOrThrowException(ProductDto productDto) {

        productCategoryService.findById(productDto.getCategory().getId());

        if (Objects.nonNull(productDto.getAge())) {
            ageService.findById(productDto.getAge().getId());
        }
        if (Objects.nonNull(productDto.getBrand())) {
            brandService.findById(productDto.getBrand().getId());
        }
        if (Objects.nonNull(productDto.getColor())) {
            colorService.findById(productDto.getColor().getId());
        }
        if (Objects.nonNull(productDto.getMaterial())) {
            materialService.findById(productDto.getMaterial().getId());
        }
        if (Objects.nonNull(productDto.getPrescription())) {
            prescriptionService.findById(productDto.getPrescription().getId());
        }
        if (Objects.nonNull(productDto.getProductSize())) {
            productSizeService.findById(productDto.getProductSize().getId());
        }
        if (Objects.nonNull(productDto.getWeight())) {
            weightService.findById(productDto.getWeight().getId());
        }
    }

    private void verifyPrices(ProductDto productDto) {

        double price = (double) Math.round(productDto.getPrice() * 100) / 100;
        double priceWithDiscount = (productDto.getPriceWithDiscount() == null) ? 0 : (double) Math.round(productDto.getPriceWithDiscount() * 100) / 100;

        if (price <= 0) {
            throw new PriceConflictException("Price should be greater than 0.00");
        } else if (price == priceWithDiscount) {
            throw new PriceConflictException("Price with discount should be different from price.");
        } else if (price < priceWithDiscount) {
            throw new PriceConflictException("Price with discount should be less then price.");
        }
    }


    //// IMAGES ////

    @Override
    public Set<AttachedFileDto> findAllImagesByOwnerId(Long productId) {
        log.debug("Finding all images by {} ID {}", ENTITY_CLASS_NAME, productId);

        ProductDto productDto = productRep.findById(productId)
                .map(productMapper::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, productId));

        Set<AttachedFileDto> attachedFilesDto = attachedImageService.findAllByOwner(productDto);

        log.debug("Found {} images by {} ID {}", attachedFilesDto.size(), ENTITY_CLASS_NAME, productId);
        return attachedFilesDto;
    }

    @Override
    public AttachedFileDto findImageByIdAndOwnerId(Long productId, Long imageId) {
        log.debug("Finding image by ID {} by {} ID {}", imageId, ENTITY_CLASS_NAME, productId);

        ProductDto productDto = productRep.findById(productId)
                .map(productMapper::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, productId));

        AttachedFileDto attachedFileDto = attachedImageService.findByIdAndOwner(imageId, productDto);

        log.debug("Found image by ID {} by {} ID {}", imageId, ENTITY_CLASS_NAME, productId);
        return attachedFileDto;
    }

    @Override
    @Transactional
    public Set<AttachedFileDto> uploadImagesByOwnerId(Long productId, List<MultipartFile> images) {
        log.debug("Uploading {} images for {} ID {}", images.size(), ENTITY_CLASS_NAME, productId);

        ProductDto productDto = productRep.findById(productId)
                .map(productMapper::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, productId));

        Set<AttachedFileDto> uploadedImages = attachedImageService.uploadFilesByOwner(productDto, images);

        log.debug("Uploaded for {} ID {} images with IDs {}", ENTITY_CLASS_NAME, productId,
                uploadedImages.stream().map(AttachedFileDto::getId).collect(Collectors.toList()));

        return uploadedImages;
    }

    @Override
    @Transactional
    public void deleteAllImagesByOwnerId(Long productId) {
        log.debug("Deleting all images for {} ID {}", ENTITY_CLASS_NAME, productId);

        Product product = productRep.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, productId));

        product.setMainImage(null);

        attachedImageService.deleteAllByOwner(productMapper.mapToDto(product));

        log.debug("Deleted all images for {} ID {}", ENTITY_CLASS_NAME, productId);
    }

    @Override
    @Transactional
    public void deleteImageByIdAndOwnerId(Long productId, Long imageId) {
        log.debug("Deleting image by ID {} for {} ID {}", imageId, ENTITY_CLASS_NAME, productId);

        Product product = productRep.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, productId));

        if (Objects.nonNull(product.getMainImage()) && product.getMainImage().getId().equals(imageId)) {
            log.debug("Found image by ID {} for {} ID {} is the main image. Deleting main image", imageId, ENTITY_CLASS_NAME, productId);
            product.setMainImage(null);
        }
        attachedImageService.deleteByIdAndOwner(imageId, productMapper.mapToDto(product));

        log.debug("Deleted image by ID {} for {} ID {}", imageId, ENTITY_CLASS_NAME, productId);
    }
}
