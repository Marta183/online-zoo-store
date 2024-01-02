package kms.onlinezoostore.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import kms.onlinezoostore.dto.user.UserResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@RequiredArgsConstructor
@Schema(name = "ShoppingCart")
public class CartDto {
    @JsonIgnore
    private final Long id;

    @NotNull(message = "User should not be null")
    private final UserResponse user;

    private final List<CartItemDto> items;

    private final double totalPrice;
}