package kms.onlinezoostore.controllers.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kms.onlinezoostore.dto.CartDto;
import kms.onlinezoostore.dto.CartItemDto;
import kms.onlinezoostore.services.CartService;
import kms.onlinezoostore.utils.UsersUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

@RestController
@Tag(name = "Shopping carts")
@Validated
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

    @PostMapping("/items")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Put items into the cart", description = "Put items into the cart for current user")
    public void addItems(@RequestBody List<CartItemDto> cartItemRequest,
                        @AuthenticationPrincipal UserDetails userDetails) {
        cartService.addItemsToCart(cartItemRequest, UsersUtil.extractUser(userDetails));
    }

    @DeleteMapping("/items/{productId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Delete item from the cart", description = "Delete item from the cart by product id")
    public void deleteItem(@PathVariable Long productId,
                           @AuthenticationPrincipal UserDetails userDetails) {
        cartService.deleteItemFromCart(productId, UsersUtil.extractUser(userDetails));
    }

    @DeleteMapping("/items")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Clear cart for current user", description = "Clear all items from the cart for current user")
    public void deleteAllItems(@AuthenticationPrincipal UserDetails userDetails) {
        cartService.clearCartByUser(UsersUtil.extractUser(userDetails));
    }
}
