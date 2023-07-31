package kms.onlinezoostore.controllers.rest;

import kms.onlinezoostore.entities.Brand;
import kms.onlinezoostore.services.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = BrandController.REST_URL)
//@Api(description = "Controller for brands")
public class BrandController {
    static final String REST_URL = "/api/v1/brands";
    private final BrandService brandService;

    @Autowired
    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Brand> findAll() {
        return brandService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Brand findById(@PathVariable Long id) {
        return brandService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Brand create(@RequestBody Brand brand) {
        return brandService.create(brand);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Brand update(@PathVariable Long id, @RequestBody Brand brand) {
        return brandService.update(id, brand);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        brandService.deleteById(id);
    }
}
