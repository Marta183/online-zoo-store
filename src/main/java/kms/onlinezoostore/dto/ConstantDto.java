package kms.onlinezoostore.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import kms.onlinezoostore.entities.enums.ConstantKeys;
import kms.onlinezoostore.services.files.images.AttachedImageOwner;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@Getter
@ToString
@AllArgsConstructor
@Schema(name = "Constant")
public class ConstantDto implements AttachedImageOwner {

    @JsonIgnore
    private final Long id; // presents only due to each AttachedImageOwner must have Long(!) id

    @NotNull
    private final ConstantKeys key;

    private final Object value;

    @Override
    @JsonIgnore
    public String getImageOwnerClassName() {
        return "Constant";
    }

    @Override
    public String toStringImageOwner() {
        return getImageOwnerClassName() + " key = " + getKey();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConstantDto that = (ConstantDto) o;
        return Objects.equals(getId(), that.getId())
                && getKey() == that.getKey();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getKey());
    }
}
