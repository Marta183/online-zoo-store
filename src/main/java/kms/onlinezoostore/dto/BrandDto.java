package kms.onlinezoostore.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import kms.onlinezoostore.services.files.images.AttachedImageOwner;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class BrandDto implements AttachedImageOwner {

    private final Long id;

    @NotBlank(message = "Name should not be empty")
    @Size(max = 60, message = "Name should be less then 60 characters")
    private final String name;

    @Override
    @JsonIgnore
    public String getImageOwnerClassName() {
        return "Brand";
    }
}
