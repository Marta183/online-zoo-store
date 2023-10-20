package kms.onlinezoostore.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import kms.onlinezoostore.services.files.images.AttachedImageOwner;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@Getter
@ToString
@AllArgsConstructor
public class BrandDto implements AttachedImageOwner {

    private final Long id;

    @NotBlank(message = "Name should not be empty")
    @Size(max = 60, message = "Name should be less then 60 characters")
    private final String name;

    private final AttachedFileDto image;

    @Override
    @JsonIgnore
    public String getImageOwnerClassName() {
        return "Brand";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BrandDto brandDto = (BrandDto) o;
        return Objects.equals(getId(), brandDto.getId())
                && Objects.equals(getName(), brandDto.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName());
    }
}
