package kms.onlinezoostore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ProductCharacteristicDto {
    private final List<AgeDto> age;
    private final List<BrandDto> brand;
    private final List<ColorDto> color;
    private final List<MaterialDto> material;
    private final List<PrescriptionDto> prescription;
    private final List<ProductCategoryDto> category;
    private final List<ProductSizeDto> size;
    private final List<WeightDto> weight;
}

