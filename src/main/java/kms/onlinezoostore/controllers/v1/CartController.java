package kms.onlinezoostore.controllers.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import kms.onlinezoostore.dto.CartDto;
import kms.onlinezoostore.services.CartService;
import kms.onlinezoostore.utils.UsersUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

@RestController
@Tag(name = "Shopping carts")
@RequiredArgsConstructor
@RequestMapping(value = CartController.REST_URL)
public class CartController {

    static final String REST_URL = "/api/v1/carts";
    private final CartService cartService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get cart for current user")
    public CartDto findCart(@AuthenticationPrincipal UserDetails userDetails) {
        return cartService.findByUser(UsersUtil.extractUser(userDetails));
    }

    @PostMapping("/items/{productId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Add item to the cart", description = "Add new product to the cart for current user")
    public CartDto addItem(@PathVariable Long productId,
                           @RequestParam @Positive Integer quantity, //TODO test with zero
                           @AuthenticationPrincipal UserDetails userDetails) {
        return cartService.addItemToCart(productId, quantity, UsersUtil.extractUser(userDetails));
    }

    @PutMapping("/items/{productId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update item quantity by item id", description = "Update item quantity by item id")
    public CartDto updateItemQuantity(@PathVariable Long productId,
                                      @RequestParam @Positive Integer updatedQuantity,
                                      @AuthenticationPrincipal UserDetails userDetails) {
        return cartService.updateItemQuantity(productId, updatedQuantity, UsersUtil.extractUser(userDetails));
    }

    @DeleteMapping("/items/{productId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Delete item from the cart", description = "Delete item from the cart by product id")
    public CartDto deleteItem(@PathVariable Long productId,
                              @AuthenticationPrincipal UserDetails userDetails) {
        return cartService.deleteItemFromCart(productId, UsersUtil.extractUser(userDetails));
    }

    @DeleteMapping("/{email}/items")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Clear cart by user email", description = "Clear all items from the cart by user email")
    public CartDto deleteAllItems(@AuthenticationPrincipal UserDetails userDetails) {
        return cartService.clearCartByUser(UsersUtil.extractUser(userDetails));
    }
}
