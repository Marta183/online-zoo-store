package kms.onlinezoostore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class WeightDto {

    private final Long id;

    @NotBlank(message = "Name should not be empty")
    @Size(max = 60, message = "Name should be less then 60 characters")
    private final String name;
}
