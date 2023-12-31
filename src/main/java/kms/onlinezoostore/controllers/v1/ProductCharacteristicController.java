package kms.onlinezoostore.controllers.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kms.onlinezoostore.dto.ProductCharacteristicDto;
import kms.onlinezoostore.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Product characteristics")
@RequiredArgsConstructor
@RequestMapping(value = ProductCharacteristicController.REST_URL)
public class ProductCharacteristicController {
    static final String REST_URL = "/api/v1/product-characteristics";
    private final AgeService ageService;
    private final BrandService brandService;
    private final ColorService colorService;
    private final MaterialService materialService;
    private final PrescriptionService prescriptionService;
    private final ProductCategoryService productCategoryService;
    private final ProductSizeService productSizeService;
    private final WeightService weightService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all product characteristics",
               description = "Retrieve lists of all product characteristics including " +
                             "ages, brands, colors, materials, prescriptions, categories, sizes, weights")
    public ProductCharacteristicDto findAll() {
        return ProductCharacteristicDto.builder()
                .ages(ageService.findAll())
                .brands(brandService.findAll())
                .colors(colorService.findAll())
                .materials(materialService.findAll())
                .prescriptions(prescriptionService.findAll())
                .categories(productCategoryService.findAll())
                .sizes(productSizeService.findAll())
                .weights(weightService.findAll())
                .build();
    }
}
