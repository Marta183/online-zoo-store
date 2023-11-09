package kms.onlinezoostore.controllers.v1;

import jakarta.validation.Valid;
import kms.onlinezoostore.dto.ProductSizeDto;
import kms.onlinezoostore.services.ProductSizeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = ProductSizeController.REST_URL)
public class ProductSizeController {
    static final String REST_URL = "/api/v1/product-sizes";
    private final ProductSizeService productSizeService;


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductSizeDto> findAll() {
        return productSizeService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProductSizeDto findById(@PathVariable Long id) {
        return productSizeService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductSizeDto create(@RequestBody @Valid ProductSizeDto productSizeDto) {
        return productSizeService.create(productSizeDto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable Long id, @RequestBody @Valid ProductSizeDto productSizeDto) {
        productSizeService.update(id, productSizeDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        productSizeService.deleteById(id);
    }
}
