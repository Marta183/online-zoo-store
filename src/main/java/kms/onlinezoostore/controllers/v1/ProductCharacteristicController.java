package kms.onlinezoostore.controllers.v1;

import kms.onlinezoostore.dto.ProductCharacteristicDto;
import kms.onlinezoostore.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
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
    public ResponseEntity<ProductCharacteristicDto> findAll() {

        ProductCharacteristicDto productCharacteristicDto = ProductCharacteristicDto.builder()
                .age(ageService.findAll())
                .brand(brandService.findAll())
                .color(colorService.findAll())
                .material(materialService.findAll())
                .prescription(prescriptionService.findAll())
                .category(productCategoryService.findAll())
                .size(productSizeService.findAll())
                .weight(weightService.findAll())
                .build();

        return ResponseEntity.ok(productCharacteristicDto);
    }
}
