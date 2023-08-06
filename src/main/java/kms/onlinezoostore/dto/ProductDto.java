package kms.onlinezoostore.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ProductDto {

    private final Long id;

    @NotBlank(message = "Name should not be empty.")
    @Size(max = 150, message = "Name should be less then 150 characters.")
    private final String name;

    @NotNull(message = "Price should not be empty.")
    @DecimalMin(value = "0.00", inclusive = false, message = "Price should be greater than 0.")
    private final Double price;

    @NotNull(message = "Category should not be empty.")
    private final ProductCategoryDto category;

    private final BrandDto brand;
    private final ColorDto color;
    private final MaterialDto material;
    private final AgeDto age;
    private final WeightDto weight;
    private final ProductSizeDto productSize;

    private final String description;
    private final String prescription;

    private final boolean newArrival;
    private final boolean notAvailable;

    private final LocalDateTime createdAt;
}
