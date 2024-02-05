package kms.onlinezoostore.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@Schema(name = "OrderItem")
public class OrderItemDto {
    private final Long id;

    @JsonIgnore
    private OrderDto order;

    @NotNull(message = "Address should not be empty.")
    private ProductDto product;

    @NotNull(message = "Address should not be empty.")
    private int quantity;

    @NotNull(message = "Address should not be empty.")
    private Double price;

    @NotNull(message = "Address should not be empty.")
    private Double amount;
}
