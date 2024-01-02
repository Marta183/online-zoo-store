package kms.onlinezoostore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
@Schema(name = "ShoppingCartItem")
public class CartItemDto {
    @NotNull
    private final ProductDto product;
    private final int quantity;
    private final double price;
    private final double sum;
}