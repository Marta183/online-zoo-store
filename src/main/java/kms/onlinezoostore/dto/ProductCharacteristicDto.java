package kms.onlinezoostore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ProductCharacteristicDto {
    private final List<AgeDto> ages;
    private final List<BrandDto> brands;
    private final List<ColorDto> colors;
    private final List<MaterialDto> materials;
    private final List<PrescriptionDto> prescriptions;
    private final List<ProductCategoryDto> productCategories;
    private final List<ProductSizeDto> productSizes;
    private final List<WeightDto> weights;
}

