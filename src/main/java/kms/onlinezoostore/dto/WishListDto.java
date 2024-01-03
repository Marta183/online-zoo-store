package kms.onlinezoostore.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Set;

@Getter
@ToString
@RequiredArgsConstructor
@Schema(name = "WishList")
public class WishListDto {
    @JsonIgnore
    private final Long id;
    @JsonIgnore
    private final String name;

    @NotNull(message = "User should not be null")
    private final String userEmail;

    private final Set<ProductDto> products;
}