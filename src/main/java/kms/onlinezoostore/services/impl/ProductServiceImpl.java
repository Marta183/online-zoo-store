package kms.onlinezoostore.services.impl;

import kms.onlinezoostore.dto.ProductDto;
import kms.onlinezoostore.dto.mappers.ProductMapper;
import kms.onlinezoostore.entities.Product;
import kms.onlinezoostore.exceptions.EntityNotFoundException;
import kms.onlinezoostore.repositories.ProductRepository;
import kms.onlinezoostore.repositories.specifications.ProductSpecifications;
import kms.onlinezoostore.services.ProductService;
import kms.onlinezoostore.utils.UniqueFieldService;
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
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRep;
    private final UniqueFieldService uniqueFieldService;
    private static final String ENTITY_CLASS_NAME = "Product";

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, UniqueFieldService uniqueFieldService) {
        this.productRep = productRepository;
        this.uniqueFieldService = uniqueFieldService;
    }

    @Override
    public ProductDto findById(Long id) {
        ProductDto productDto = productRep.findById(id)
                .map(ProductMapper.INSTANCE::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));
        // log
        return productDto;
    }

    @Override
    public Page<ProductDto> findPageByCategoryId(Long categoryId, Pageable pageable) {
        Page<ProductDto> page =  productRep.findAllByCategoryId(categoryId, pageable)
                .map(ProductMapper.INSTANCE::mapToDto);
        // log
        return page;
    }

    @Override
    public Page<ProductDto> findPageByMultipleCriteria(MultiValueMap<String, String> params, Pageable pageable) {
        // log
        processParamsForCriteriaBuilder(params);

        Page<ProductDto> page = productRep.findAll(ProductSpecifications.build(params), pageable)
                .map(ProductMapper.INSTANCE::mapToDto);
        // log
        return page;
    }

    @Override
    @Transactional
    public ProductDto create(ProductDto productDto) {
        // log
        uniqueFieldService.checkIsFieldValueUniqueOrElseThrow(productRep, "name", productDto.getName());
        // log
        Product product = ProductMapper.INSTANCE.mapToEntity(productDto);
        // log
        Product savedProduct = productRep.save(product);
        // log
        ProductDto savedProductDto = ProductMapper.INSTANCE.mapToDto(savedProduct);
        // log
        return savedProductDto;
    }

    @Override
    @Transactional
    public void update(Long id, ProductDto updatedProductDto) {
        // log
        Product existingProduct = productRep.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));
        // log
        if (!existingProduct.getName().equals(updatedProductDto.getName())) {
            uniqueFieldService.checkIsFieldValueUniqueOrElseThrow(productRep, "name", updatedProductDto.getName());
        }

        Product updatedProduct = ProductMapper.INSTANCE.mapToEntity(updatedProductDto);
        // log
        updatedProduct.setId(id);
        updatedProduct.setCreatedAt(existingProduct.getCreatedAt());
        // log
        productRep.save(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Product product = productRep.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));
        // log
        productRep.deleteById(id);
    }

    private void processParamsForCriteriaBuilder(MultiValueMap<String, String> params) {

        deleteInvalidParams(params);

        if (params.containsKey("minPrice")
                && params.getFirst("minPrice") != null
                && Double.parseDouble(params.getFirst("minPrice")) == 0) {
            // log
            params.remove("minPrice");
        }
    }

    private void deleteInvalidParams(MultiValueMap<String, String> source) {
        Iterator<Map.Entry<String, List<String>>> mapIterator = source.entrySet().iterator();
        while (mapIterator.hasNext()) {
            Map.Entry<String, List<String>> entry = mapIterator.next();

            entry.getValue().removeIf(str -> str == null || str.isEmpty()); // remove particular value

            if (entry.getValue().isEmpty()) {
                // log
                mapIterator.remove(); // remove map.entry
            }
        }
    }
}
