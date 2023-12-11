package kms.onlinezoostore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.ToString;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.util.Objects;

@Getter
@ToString
@RequiredArgsConstructor
@Schema(name = "AttachedFile")
public class AttachedFileDto {
    private final Long id;

    @URL
    @Size(max = 255, message = "File path should be less then 255 characters.")
    private final String filePath;

    @Size(max = 100, message = "File name should be less then 100 characters.")
    private final String fileName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttachedFileDto that = (AttachedFileDto) o;
        return Objects.equals(getId(), that.getId())
                && Objects.equals(getFilePath(), that.getFilePath())
                && Objects.equals(getFileName(), that.getFileName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getFilePath(), getFileName());
    }
}