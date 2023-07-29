package kms.onlinezoostore.controllers.rest;

import jakarta.validation.Valid;
import kms.onlinezoostore.entities.Product;
import kms.onlinezoostore.entities.ProductCategory;
import kms.onlinezoostore.services.ProductCategoryService;
import kms.onlinezoostore.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = ProductCategoryController.REST_URL)
//@Api(description = "Controller for product categories")
public class ProductCategoryController {
    static final String REST_URL = "/api/v1/product-categories";
    private final ProductCategoryService categoryService;
    private final ProductService productService;

    @Autowired
    public ProductCategoryController(ProductCategoryService categoryService, ProductService productService) {
        this.categoryService = categoryService;
        this.productService = productService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductCategory> findAll(@RequestParam(name = "parent_id", required = false) String parentId,
                                         @RequestParam(name = "name_starting_with", required = false) String nameStartingWith) {
        if (parentId != null && !parentId.isBlank()) {
            return categoryService.findAllByParentId(parentId);
        } else if (nameStartingWith != null && !nameStartingWith.isBlank()) {
            return categoryService.findAllByNameStartingWith(nameStartingWith);
        }
        return categoryService.findAll();
    }

    @GetMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.OK)
    public ProductCategory findById(@PathVariable Long categoryId) {
        return categoryService.findById(categoryId);
    }

    @GetMapping("/{categoryId}/inner-categories")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductCategory> findAllByParentId(@PathVariable String categoryId) {
        return categoryService.findAllByParentId(categoryId);
    }

    @GetMapping("/{categoryId}/products")
    @ResponseStatus(HttpStatus.OK)
    public List<Product> findProductsByCriteria(@PathVariable String categoryId,
                                                @RequestParam MultiValueMap<String, String> params) {
        params.add("category_id", categoryId);
        return productService.findByMultipleCriteria(params);
    }

//    @GetMapping("/{categoryId}/products/max-price")
//    @ResponseStatus(HttpStatus.OK)
//    public List<Product> findMaxProductPriceByCategoryId(@PathVariable Long categoryId) {
//        return
//    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductCategory create(@RequestBody @Valid ProductCategory productCategory) { //}, BindingResult bindingResult) {
        return categoryService.create(productCategory);
    }

    @PutMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.OK)
    public ProductCategory update(@PathVariable Long categoryId, @RequestBody @Valid ProductCategory productCategory) {
        return categoryService.update(categoryId, productCategory);
    }

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long categoryId) {
        categoryService.deleteById(categoryId);
    }
}
