package kms.onlinezoostore.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kms.onlinezoostore.services.files.images.AttachedImageOwner;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Getter
@ToString
@AllArgsConstructor
@Schema(name = "Product")
public class ProductDto implements AttachedImageOwner {

    private final Long id;

    @NotBlank(message = "Name should not be empty.")
    @Size(max = 150, message = "Name should be less then 150 characters.")
    private final String name;

    @NotNull(message = "Price should not be empty.")
    @Positive(message = "Price should be greater than 0.00")
    private final Double price;

    @PositiveOrZero(message = "Price with discount should be positive or 0.")
    private final Double priceWithDiscount;

    private final List<AttachedFileDto> images;
    private final AttachedFileDto mainImage;

    @NotNull(message = "Category should not be empty.")
    private final ProductCategoryDto category;

    private final BrandDto brand;
    private final ColorDto color;
    private final MaterialDto material;
    private final AgeDto age;
    private final WeightDto weight;
    private final ProductSizeDto productSize;
    private final PrescriptionDto prescription;

    private final String description;
    private final String instructions;
    private final String contraindications;

    private final boolean newArrival;
    private final boolean notAvailable;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private final LocalDateTime createdAt;

    @Override
    @JsonIgnore
    public String getImageOwnerClassName() {
        return "Product";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductDto that = (ProductDto) o;
        return Objects.equals(getId(), that.getId())
                && Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName());
    }
}
