package kms.onlinezoostore.services.impl;

import jakarta.validation.constraints.NotNull;
import kms.onlinezoostore.dto.CartDto;
import kms.onlinezoostore.dto.mappers.CartMapper;
import kms.onlinezoostore.entities.Cart;
import kms.onlinezoostore.entities.CartItem;
import kms.onlinezoostore.entities.Product;
import kms.onlinezoostore.entities.User;
import kms.onlinezoostore.exceptions.EntityDuplicateException;
import kms.onlinezoostore.exceptions.EntityNotFoundException;
import kms.onlinezoostore.exceptions.UnavailableProductException;
import kms.onlinezoostore.repositories.CartItemRepository;
import kms.onlinezoostore.repositories.CartRepository;
import kms.onlinezoostore.repositories.ProductRepository;
import kms.onlinezoostore.services.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {
    private final CartMapper cartMapper;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public CartDto findByUser(@NotNull User user) {
        log.debug("Finding cart by user with email {}", user.getEmail());

        CartDto cartDto = cartMapper.mapToDto(user.getCart());

        log.debug("Found cart by user with email {}", user.getEmail());
        return cartDto;
    }

    @Override
    public void createCartForUser(@NotNull User user) {
        String userEmail = user.getEmail();
        log.debug("Creating cart for user with email {}", userEmail);

        Cart existingCart = user.getCart();
        if (Objects.nonNull(existingCart)) {
            throw new EntityDuplicateException("Cart already exists for user email= " + userEmail);
        }
        Cart cart = new Cart();
        cart.setUser(user);
        Cart savedCart = cartRepository.save(cart);
        user.setCart(savedCart);

        log.debug("Created cart for user with email {}", userEmail);
    }

    @Override
    public void addItemToCart(Long productId, Integer quantity, @NotNull User user) {
        log.debug("Create CartItem for user with email {}", user.getEmail());

        Cart cart = user.getCart();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product", productId));
        if (product.isNotAvailable()) {
            log.info("Product with ID " + productId + " should be available before adding to the cart for user " + user.getEmail());
            throw new UnavailableProductException("Product with ID " + productId + " is not available in the system");
        }
        CartItem itemToUpdate = cart.getItemByProductId(productId);
        if (Objects.isNull(itemToUpdate)) {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            CartItem savedItem = cartItemRepository.save(newItem);

            cart.getItems().add(savedItem);
        } else {
            log.debug("Item with product id {} already exists in the cart with user email {}", productId, user.getEmail());
            itemToUpdate.setQuantity(quantity);
            cartItemRepository.save(itemToUpdate);
        }
        log.debug("Created CartItem for user with email {}", user.getEmail());
    }

    @Override
    public void deleteItemFromCart(Long productId, @NotNull User user) {
        log.debug("Deleting CartItem by product ID {} and user email {}", productId, user.getEmail());

        Cart cart = user.getCart();
        cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));
        cartRepository.save(cart);

        log.debug("Deleted CartItem by product ID {} and user email {}", productId, user.getEmail());
    }

    @Override
    public void clearCartByUser(@NotNull User user) {
        log.debug("Clear cart for user with email {}", user.getEmail());

        Cart cart = user.getCart();
        cart.setItems(new ArrayList<>());
        cartRepository.save(cart);

        log.debug("Cleared cart for user with email {}", user.getEmail());
    }
}
