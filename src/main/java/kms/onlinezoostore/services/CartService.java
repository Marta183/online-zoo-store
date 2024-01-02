package kms.onlinezoostore.services;

import kms.onlinezoostore.dto.CartDto;
import kms.onlinezoostore.entities.User;

public interface CartService {

    CartDto findByUser(User user);

    CartDto createCartForUser(User user);

    CartDto clearCartByUser(User user);

    CartDto addItemToCart(Long productId, Integer quantity, User user);
    CartDto updateItemQuantity(Long productId, Integer updatedQuantity, User user);
    CartDto deleteItemFromCart(Long productId, User user);
}
