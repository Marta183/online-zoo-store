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

        deleteInvalidParams(params);

        return productRep.findAll(ProductSpecifications.build(params));
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
        updatedProduct.setId(id);
        return productRep.save(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        productRep.deleteById(id);
    }


    //TODO: check if it works =) and then delete
//    @Override
//    public List<Product> findAllByParameters(String name, Long idCategory, Double price1, Double price2,
//                                             Boolean newArrival, Boolean archived,
//                                             Integer page, Integer productsPerPage) {
//
//        boolean usePriceFilter = (price1 != null || price2 != null);
//        price1 = (price1 == null) ? 0 : price1;
//        price2 = (price2 == null) ? Double.MAX_VALUE : price2;
//
//        return productRep.findAllByParameters(PageRequest.of(page, productsPerPage),
//                        name, idCategory, usePriceFilter, price1, price2, newArrival, archived)
//                .getContent();
//    }


//    public List<Product> findProductsByMultipleCriteria(String category, String size, String packaging, int age, String material) {
//        List<Specification<Product>> specs = new ArrayList<>();
//
//        if (category != null)
//            specs.add(ProductSpecifications.hasCategory(category));
//        if (size != null)
//            specs.add(ProductSpecifications.hasSize(size));
//        if (packaging != null)
//            specs.add(ProductSpecifications.hasPackaging(packaging));
//        if (age != 0)
//            specs.add(ProductSpecifications.hasAge(age));
//        if (material != null)
//            specs.add(ProductSpecifications.hasMaterial(material));
//        Specification<Product> combinedSpec = specs.stream().reduce(Specification::and).orElse(null);
//        return productRep.findAll(combinedSpec);
//    }

}
