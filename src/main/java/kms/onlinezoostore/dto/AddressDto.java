package kms.onlinezoostore.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@Getter
@ToString
@AllArgsConstructor
@Schema(name = "Address")
public class AddressDto {

    @JsonIgnore
    private final Long id;

    private final String street;

    private final String houseNumber;

    private final String apartment;

    @NotBlank(message = "City should not be empty.")
    private final String city;

    @NotBlank(message = "Country should not be empty.")
    private final String country;

    @NotBlank(message = "Postal code should not be empty.")
    private final String postalCode;

    private final String postalDepartmentNumber;
}
