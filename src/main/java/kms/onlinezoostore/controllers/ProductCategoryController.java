package kms.onlinezoostore.controllers;

import jakarta.validation.Valid;
import kms.onlinezoostore.dto.ProductCategoryDto;
import kms.onlinezoostore.dto.ProductDto;
import kms.onlinezoostore.services.ProductCategoryService;
import kms.onlinezoostore.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = ProductCategoryController.REST_URL)
public class ProductCategoryController {
    static final String REST_URL = "/api/v1/product-categories";

    private final ProductCategoryService thisService;
    private final ProductService productService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductCategoryDto> findAll(
            @RequestParam(name = "nameLike", required = false) String nameLike) {
        if (nameLike != null && !nameLike.isBlank()) {
            return thisService.findAllByNameLike(nameLike);
        }
        return thisService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProductCategoryDto findById(@PathVariable Long id) {
        return thisService.findById(id);
    }

    @GetMapping("/{id}/inner-categories")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductCategoryDto> findAllByParentId(@PathVariable Long id) {
        return thisService.findAllByParentId(id);
    }

    @GetMapping("/{id}/products")
    @ResponseStatus(HttpStatus.OK)
    public Page<ProductDto> findProductPageByCriteria(@PathVariable Long id, Pageable pageable,
                                                      @RequestParam MultiValueMap<String, String> params) {
        params.remove("pageNumber");
        params.remove("pageSize");
        params.remove("sort");

        if (params.isEmpty()) {
            return productService.findPageByCategoryId(id, pageable);
        }

        params.remove("categoryId"); // acceptably only as a pathVariable
        params.add("categoryId", id.toString());
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

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable Long id, @RequestBody @Valid ProductCategoryDto productCategoryDto) {
        thisService.update(id, productCategoryDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        thisService.deleteById(id);
    }
}
