package kms.onlinezoostore.controllers.rest;

import jakarta.validation.Valid;
import kms.onlinezoostore.entities.ProductSize;
import kms.onlinezoostore.services.ProductSizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = ProductSizeController.REST_URL)
//@Api(description = "Controller for product sizes")
public class ProductSizeController {
    static final String REST_URL = "/api/v1/product-sizes";
    private final ProductSizeService productSizeService;

    @Autowired
    public ProductSizeController(ProductSizeService productSizeService) {
        this.productSizeService = productSizeService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductSize> findAll() {
        return productSizeService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProductSize findById(@PathVariable Long id) {
        return productSizeService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductSize create(@RequestBody @Valid ProductSize productSize) {
        return productSizeService.create(productSize);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProductSize update(@PathVariable Long id, @RequestBody @Valid ProductSize productSize) {
        return productSizeService.update(id, productSize);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        productSizeService.deleteById(id);
    }
}
