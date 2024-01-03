package kms.onlinezoostore.services;

import kms.onlinezoostore.dto.CartDto;
import kms.onlinezoostore.entities.User;

public interface CartService {

    CartDto findByUser(User user);

    void createCartForUser(User user);

    void addItemToCart(Long productId, Integer quantity, User user);
    void deleteItemFromCart(Long productId, User user);
    void clearCartByUser(User user);
}
