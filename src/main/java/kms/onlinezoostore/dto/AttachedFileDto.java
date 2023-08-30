package kms.onlinezoostore.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.ToString;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.util.Objects;

@Getter
@ToString
@RequiredArgsConstructor
public class AttachedFileDto {
    private final Long id;

    @URL
    @Size(max = 255, message = "File path should be less then 255 characters.")
    private final String filePath;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttachedFileDto that = (AttachedFileDto) o;
        // using both fields as filePath shouldn't be changed for particular id
        return Objects.equals(getId(), that.getId())
                && Objects.equals(getFilePath(), that.getFilePath());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getFilePath());
    }
}