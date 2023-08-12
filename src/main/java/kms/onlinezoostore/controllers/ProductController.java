package kms.onlinezoostore.controllers;

import jakarta.validation.Valid;
import kms.onlinezoostore.dto.ProductDto;
import kms.onlinezoostore.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = ProductController.REST_URL)
public class ProductController {
    static final String REST_URL = "/api/v1/products";
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProductDto findProductById(@PathVariable Long id) {
        return productService.findById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<ProductDto> findPageByCriteria(@RequestParam MultiValueMap<String, String> params, Pageable pageable) {
        params.remove("pageNumber");
        params.remove("pageSize");
        params.remove("sort");

        if (params.isEmpty()) {
            return productService.findPage(pageable);
        }
        return productService.findPageByMultipleCriteria(params, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDto createProduct(@RequestBody @Valid ProductDto productDto) { //}, BindingResult bindingResult) {
        return productService.create(productDto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateProduct(@PathVariable Long id, @RequestBody @Valid ProductDto productDto) {
        productService.update(id, productDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProductById(@PathVariable Long id) {
        productService.deleteById(id);
    }
}
