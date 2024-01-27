package kms.onlinezoostore.controllers.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kms.onlinezoostore.dto.WishListDto;
import kms.onlinezoostore.services.WishListService;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.Set;

@RestController
@Tag(name = "Wish lists")
@Validated
@RequiredArgsConstructor
@RequestMapping(value = WishListController.REST_URL)
public class WishListController {

    static final String REST_URL = "/api/v1/wish-lists";
    private final WishListService wishListService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get default wish list for current user")
    public WishListDto findWishList(@AuthenticationPrincipal UserDetails userDetails) {
        return wishListService.findByUser(UsersUtil.extractUser(userDetails));
    }

    @PostMapping("/items")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Put items into wish list", description = "Put list of items into wish list for current user")
    public void addItems(@RequestBody Set<Long> productIds,
                         @AuthenticationPrincipal UserDetails userDetails) {
        wishListService.addItems(productIds, UsersUtil.extractUser(userDetails));
    }

    @PutMapping("/items/refill")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Refill wish list with new items",
               description = "Delete all existed items and add new into wish list for current user")
    public void refillWishList(@RequestBody Set<Long> productIds,
                               @AuthenticationPrincipal UserDetails userDetails) {
        wishListService.refillWishList(productIds, UsersUtil.extractUser(userDetails));
    }

    @DeleteMapping("/items/{productId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Delete item from wish list", description = "Delete item from wish list by product id")
    public void deleteItem(@PathVariable Long productId,
                           @AuthenticationPrincipal UserDetails userDetails) {
        wishListService.deleteItem(productId, UsersUtil.extractUser(userDetails));
    }

    @DeleteMapping("/items")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Clear wish list for current user", description = "Clear all items from wish list for current user")
    public void deleteItems(@RequestBody Set<Long> productIds,
                            @AuthenticationPrincipal UserDetails userDetails) {
        wishListService.deleteItems(productIds, UsersUtil.extractUser(userDetails));
    }

    @DeleteMapping("/items/all")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Clear wish list for current user", description = "Clear all items from wish list for current user")
    public void deleteAllItems(@AuthenticationPrincipal UserDetails userDetails) {
        wishListService.deleteAllItems(UsersUtil.extractUser(userDetails));
    }
}
