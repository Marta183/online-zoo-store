package kms.onlinezoostore.controllers;

import jakarta.validation.Valid;
import kms.onlinezoostore.dto.ProductCategoryDto;
import kms.onlinezoostore.dto.ProductDto;
import kms.onlinezoostore.services.ProductCategoryService;
import kms.onlinezoostore.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = ProductCategoryController.REST_URL)
//@Api(description = "Controller for product categories")
public class ProductCategoryController {
    static final String REST_URL = "/api/v1/product-categories";

    @Autowired
    private ProductCategoryService thisService;

    @Autowired
    private ProductService productService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductCategoryDto> findAll(@RequestParam(name = "nameLike", required = false) String nameLike) {
        if (nameLike != null && !nameLike.isBlank()) {
            return thisService.findAllByNameLike(nameLike);
        }
        return thisService.findAll();
    }

    @GetMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.OK)
    public ProductCategoryDto findById(@PathVariable Long categoryId) {
        return thisService.findById(categoryId);
    }

    @GetMapping("/{categoryId}/inner-categories")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductCategoryDto> findAllByParentId(@PathVariable Long categoryId) {
        return thisService.findAllByParentId(categoryId);
    }

    @GetMapping("/{categoryId}/products")
    @ResponseStatus(HttpStatus.OK)
    public Page<ProductDto> findProductPageByCriteria(@PathVariable Long categoryId, Pageable pageable,
                                                         @RequestParam MultiValueMap<String, String> params) {
        params.remove("pageNumber");
        params.remove("pageSize");
        params.remove("sort");

        if (params.isEmpty()) {
            return productService.findPageByCategoryId(categoryId, pageable);
        }

        params.add("categoryId", categoryId.toString());
        return productService.findPageByMultipleCriteria(params, pageable);
    }

//    @GetMapping("/{categoryId}/products/max-price")
//    @ResponseStatus(HttpStatus.OK)
//    public List<Product> findMaxProductPriceByCategoryId(@PathVariable Long categoryId) {
//        return
//    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductCategoryDto create(@RequestBody @Valid ProductCategoryDto productCategoryDto) { //}, BindingResult bindingResult) {
        return thisService.create(productCategoryDto);
    }

    @PutMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable Long categoryId, @RequestBody @Valid ProductCategoryDto productCategoryDto) {
        thisService.update(categoryId, productCategoryDto);
    }

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long categoryId) {
        thisService.deleteById(categoryId);
    }
}
