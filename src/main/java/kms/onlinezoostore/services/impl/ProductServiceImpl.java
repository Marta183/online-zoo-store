package kms.onlinezoostore.services.impl;

import kms.onlinezoostore.entities.Product;
import kms.onlinezoostore.repositories.ProductRepository;
import kms.onlinezoostore.repositories.specifications.ProductSpecifications;
import kms.onlinezoostore.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRep = productRepository;
    }

    @Override
    public Product findById(Long id) {
        return productRep.findById(id).orElse(null);
    }

    @Override
    public List<Product> findAllByCategoryId(Long categoryId) {
        return productRep.findAllByCategory_Id(categoryId);
    }

    @Override
    public List<Product> findByMultipleCriteria(MultiValueMap<String, String> params) {

        processParamsForCriteriaBuilder(params);

        return productRep.findAll(ProductSpecifications.build(params));
    }

    private void processParamsForCriteriaBuilder(MultiValueMap<String, String> params) {

        deleteInvalidParams(params);

        if (params.containsKey("min_price")
                && params.getFirst("min_price") != null
                && Double.parseDouble(params.getFirst("min_price")) == 0) {
            params.remove("min_price");
        }
    }

    private void deleteInvalidParams(MultiValueMap<String, String> source) {
        Iterator<Map.Entry<String, List<String>>> mapIterator = source.entrySet().iterator();
        while (mapIterator.hasNext()) {
            Map.Entry<String, List<String>> entry = mapIterator.next();

            entry.getValue().removeIf(str -> str == null || str.isEmpty()); // remove particular value

            if (entry.getValue().isEmpty()) {
                mapIterator.remove(); // remove map.entry
            }
        }
    }

    @Override
    @Transactional
    public Product create(Product product) {
        return productRep.save(product);
    }

    @Override
    @Transactional
    public Product update(Long id, Product updatedProduct) {
        Product product = productRep.findById(id).orElse(null);
        if (product == null) {
            return null; //TODO: exception
        }

        updatedProduct.setId(id);
        updatedProduct.setCreatedAt(product.getCreatedAt());
        return productRep.save(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        productRep.deleteById(id);
    }
}
