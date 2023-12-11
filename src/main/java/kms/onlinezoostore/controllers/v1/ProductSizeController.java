package kms.onlinezoostore.controllers.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kms.onlinezoostore.dto.ProductSizeDto;
import kms.onlinezoostore.services.ProductSizeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RestController
@Tag(name = "Sizes")
@RequiredArgsConstructor
@RequestMapping(value = ProductSizeController.REST_URL)
public class ProductSizeController {
    static final String REST_URL = "/api/v1/product-sizes";
    private final ProductSizeService productSizeService;


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all sizes", description = "Get a list of all sizes")
    public List<ProductSizeDto> findAll() {
        return productSizeService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get size by ID", description = "Get size details by ID")
    public ProductSizeDto findById(@PathVariable Long id) {
        return productSizeService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new size", description = "Create a new size with the provided details")
    public ProductSizeDto create(@RequestBody @Valid ProductSizeDto productSizeDto) {
        return productSizeService.create(productSizeDto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update size by ID", description = "Update an existing size by ID and details")
    public void update(@PathVariable Long id, @RequestBody @Valid ProductSizeDto productSizeDto) {
        productSizeService.update(id, productSizeDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete size by ID", description = "Delete an existing size by ID")
    public void deleteById(@PathVariable Long id) {
        productSizeService.deleteById(id);
    }
}
