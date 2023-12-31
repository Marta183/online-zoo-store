package kms.onlinezoostore.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "Color")
public class ColorDto implements AttachedImageOwner {

    private final Long id;

    @NotBlank(message = "Name should not be empty")
    @Size(max = 60, message = "Name should be less then 60 characters")
    private final String name;

    private final AttachedFileDto image;

    @Override
    @JsonIgnore
    public String getImageOwnerClassName() {
        return "Color";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColorDto colorDto = (ColorDto) o;
        return Objects.equals(getId(), colorDto.getId())
                && Objects.equals(getName(), colorDto.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName());
    }
}
