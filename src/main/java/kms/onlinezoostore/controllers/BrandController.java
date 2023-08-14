package kms.onlinezoostore.controllers;

import jakarta.validation.Valid;
import kms.onlinezoostore.dto.BrandDto;
import kms.onlinezoostore.dto.ProductDto;
import kms.onlinezoostore.services.BrandService;
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
@RequestMapping(value = BrandController.REST_URL)
public class BrandController {
    static final String REST_URL = "/api/v1/brands";

    private final BrandService brandService;
    private final ProductService productService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BrandDto> findAll() {
        return brandService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BrandDto findById(@PathVariable Long id) {
        return brandService.findById(id);
    }

    @GetMapping("/{id}/products")
    @ResponseStatus(HttpStatus.OK)
    public Page<ProductDto> findProductPageByCriteria(@PathVariable Long id, Pageable pageable,
                                                      @RequestParam MultiValueMap<String, String> params) {
        params.remove("pageNumber");
        params.remove("pageSize");
        params.remove("sort");

        if (params.isEmpty()) {
            return productService.findPageByBrandId(id, pageable);
        }

        params.remove("brandId"); // acceptably only as a pathVariable
        params.add("brandId", id.toString());
        return productService.findPageByMultipleCriteria(params, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BrandDto create(@RequestBody @Valid BrandDto brandDto) {
        return brandService.create(brandDto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable Long id, @RequestBody @Valid BrandDto brandDto) {
        brandService.update(id, brandDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        brandService.deleteById(id);
    }
}
