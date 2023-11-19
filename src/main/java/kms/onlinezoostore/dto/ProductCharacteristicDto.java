package kms.onlinezoostore.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ProductCharacteristicDto {
    @JsonProperty("age")
    private final List<AgeDto> ages;

    @JsonProperty("brand")
    private final List<BrandDto> brands;

    @JsonProperty("color")
    private final List<ColorDto> colors;

    @JsonProperty("material")
    private final List<MaterialDto> materials;

    @JsonProperty("prescription")
    private final List<PrescriptionDto> prescriptions;

    @JsonProperty("category")
    private final List<ProductCategoryDto> categories;

    @JsonProperty("size")
    private final List<ProductSizeDto> sizes;

    @JsonProperty("weight")
    private final List<WeightDto> weights;
}

