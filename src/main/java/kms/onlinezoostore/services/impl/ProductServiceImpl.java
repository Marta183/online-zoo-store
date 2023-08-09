package kms.onlinezoostore.services.impl;

import kms.onlinezoostore.dto.ProductDto;
import kms.onlinezoostore.dto.mappers.ProductMapper;
import kms.onlinezoostore.entities.Product;
import kms.onlinezoostore.exceptions.EntityNotFoundException;
import kms.onlinezoostore.repositories.ProductRepository;
import kms.onlinezoostore.repositories.specifications.ProductSpecifications;
import kms.onlinezoostore.services.ProductService;
import kms.onlinezoostore.utils.UniqueFieldService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRep;
    private final UniqueFieldService uniqueFieldService;
    private static final String ENTITY_CLASS_NAME = "PRODUCT";

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, UniqueFieldService uniqueFieldService) {
        this.productRep = productRepository;
        this.uniqueFieldService = uniqueFieldService;
    }

    @Override

    public ProductDto findById(Long id) {
        log.debug("Finding {} by ID {}", ENTITY_CLASS_NAME, id);

        ProductDto productDto = productRep.findById(id)
                .map(ProductMapper.INSTANCE::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));

        log.debug("Found {} by ID {}", ENTITY_CLASS_NAME, id);
        return productDto;
    }

    @Override
    public Page<ProductDto> findPageByCategoryId(Long categoryId, Pageable pageable) {
        log.debug("Finding {} page by category ID {}", ENTITY_CLASS_NAME, categoryId);
        log.debug("  with page number: {}, page size: {}", pageable.getPageNumber(), pageable.getPageSize());

        Page<ProductDto> page =  productRep.findAllByCategoryId(categoryId, pageable)
                .map(ProductMapper.INSTANCE::mapToDto);

        log.debug("Found {} page with number of products: {}", ENTITY_CLASS_NAME, page.getContent().size());
        return page;
    }

    @Override
    public Page<ProductDto> findPageByMultipleCriteria(MultiValueMap<String, String> params, Pageable pageable) {
        log.debug("Finding {} page by multiple criteria", ENTITY_CLASS_NAME);
        log.debug("  with page number: {}, page size: {}", pageable.getPageNumber(), pageable.getPageSize());

        processParamsForCriteriaBuilder(params);

        Page<ProductDto> page = productRep.findAll(ProductSpecifications.build(params), pageable)
                .map(ProductMapper.INSTANCE::mapToDto);

        log.debug("Found {} page with number of products: {}", ENTITY_CLASS_NAME, page.getContent().size());
        return page;
    }

    @Override
    @Transactional
    public ProductDto create(ProductDto productDto) {
        log.debug("Creating a new {}: {}", ENTITY_CLASS_NAME, productDto.getName());

        uniqueFieldService.checkIsFieldValueUniqueOrElseThrow(productRep, "name", productDto.getName());

        Product product = ProductMapper.INSTANCE.mapToEntity(productDto);

        Product savedProduct = productRep.save(product);
        log.debug("New {} saved in DB with ID {}", ENTITY_CLASS_NAME, savedProduct.getId());

        return ProductMapper.INSTANCE.mapToDto(savedProduct);
    }

    @Override
    @Transactional
    public void update(Long id, ProductDto updatedProductDto) {
        log.debug("Updating {} with ID {}", ENTITY_CLASS_NAME, id);

        Product existingProduct = productRep.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));

        if (!existingProduct.getName().equals(updatedProductDto.getName())) {
            uniqueFieldService.checkIsFieldValueUniqueOrElseThrow(productRep, "name", updatedProductDto.getName());
        }

        Product updatedProduct = ProductMapper.INSTANCE.mapToEntity(updatedProductDto);

        updatedProduct.setId(id);
        updatedProduct.setCreatedAt(existingProduct.getCreatedAt());
        productRep.save(updatedProduct);

        log.debug("{} with ID {} updated in DB", ENTITY_CLASS_NAME, id);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.debug("Deleting {} with ID {}", ENTITY_CLASS_NAME, id);

        productRep.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));

        productRep.deleteById(id);
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
}
