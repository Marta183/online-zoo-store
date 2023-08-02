package kms.onlinezoostore.services.impl;

import kms.onlinezoostore.entities.Product;
import kms.onlinezoostore.exceptions.EntityNotFoundException;
import kms.onlinezoostore.repositories.ProductRepository;
import kms.onlinezoostore.repositories.specifications.ProductSpecifications;
import kms.onlinezoostore.services.ProductService;
import kms.onlinezoostore.utils.UniqueFieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    public Product findById(Long id) {
        Product product = productRep.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));
        // log
        return product;
    }

    @Override
    public Page<Product> findAllByCategoryId(Long categoryId, Integer pageNumber, Integer pageSize) {
        return productRep.findAllByCategory_Id(categoryId, PageRequest.of(pageNumber -1, pageSize));
    }

    @Override
    public Page<Product> findPageByMultipleCriteria(MultiValueMap<String, String> params, Integer pageNumber, Integer pageSize) {
        // log
        processParamsForCriteriaBuilder(params);

        Page<Product> page = productRep.findAll(
                ProductSpecifications.build(params),
                PageRequest.of(pageNumber -1, pageSize)); // (pageIndex - 1) because here the numbering is from 0, and at the front from 1
        // log
        return page;
    }

    @Override
    @Transactional
    public Product create(Product product) {
        // log
        uniqueFieldService.checkIsFieldValueUniqueOrElseThrow(productRep, "name", product.getName());
        // log
        Product savedProduct = productRep.save(product);
        // log
        return savedProduct;
    }

    @Override
    @Transactional
    public Product update(Long id, Product updatedProduct) {
        // log
        Product existingProduct = productRep.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));
        // log
        if (!existingProduct.getName().equals(updatedProduct.getName())) {
            uniqueFieldService.checkIsFieldValueUniqueOrElseThrow(productRep, "name", updatedProduct.getName());
        }
        updatedProduct.setId(id);
        updatedProduct.setCreatedAt(existingProduct.getCreatedAt());

        return productRep.save(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
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
