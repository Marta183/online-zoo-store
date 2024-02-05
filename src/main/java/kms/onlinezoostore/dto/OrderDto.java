package kms.onlinezoostore.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import kms.onlinezoostore.dto.user.UserResponse;
import kms.onlinezoostore.entities.OrderItem;
import kms.onlinezoostore.entities.enums.DeliveryMethod;
import kms.onlinezoostore.entities.enums.DeliveryService;
import kms.onlinezoostore.entities.enums.OrderStatus;
import kms.onlinezoostore.entities.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString
@AllArgsConstructor
@Schema(name = "Order")
public class OrderDto {
    private final Long id;

    @NotNull(message = "User should not be null.")
    private final UserResponse user;

    @Pattern(regexp = "^\\+\\d+$", message = "Invalid phone number format")
    private final String phoneNumber;

    @NotEmpty(message = "Status should not be empty.")
    private final OrderStatus status;

    private final double totalAmount;

    @NotEmpty(message = "Payment method should not be empty.")
    private final PaymentMethod paymentMethod;

    @NotEmpty(message = "Delivery service should not be empty.")
    private final DeliveryService deliveryService;

    @NotEmpty(message = "Delivery method should not be empty.")
    private final DeliveryMethod deliveryMethod;

    @NotNull(message = "Address should not be empty.")
    private final AddressDto deliveryAddress;

    private final String deliveryNumber;

    private final String comment;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private final LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private final LocalDateTime completedAt;

    @NotNull(message = "Status should not be empty.")
    private final List<OrderItemDto> items;

    public double calculateTotalAmount() {
        return this.items.stream()
                .mapToDouble(OrderItemDto::getAmount)
                .sum();
    }
}