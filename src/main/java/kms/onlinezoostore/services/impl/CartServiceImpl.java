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
    public CartDto createCartForUser(@NotNull User user) {
        String userEmail = user.getEmail();
        log.debug("Creating cart for user with email {}", userEmail);

        Cart existingCart = user.getCart();
        if (Objects.nonNull(existingCart)) {
            throw new EntityDuplicateException("Cart already exists for user email= " + userEmail);
        }
        Cart cart = new Cart();
        cart.setUser(user);
        Cart savedCart = cartRepository.save(cart);
        CartDto savedCartDto = cartMapper.mapToDto(savedCart);

        user.setCart(savedCart);

        log.debug("Created cart for user with email {}", userEmail);
        return savedCartDto;
    }

    @Override
    public CartDto clearCartByUser(@NotNull User user) {
        log.debug("Clear cart for user with email {}", user.getEmail());

        Cart cart = user.getCart();
        cart.setItems(new ArrayList<>()); //TODO: check if items will be deleted
        CartDto cartDto = cartMapper.mapToDto(cart);

        log.debug("Cleared cart for user with email {}", user.getEmail());
        return cartDto;
    }

    @Override
    public CartDto addItemToCart(Long productId, Integer quantity, @NotNull User user) {
        log.debug("Create CartItem for user with email {}", user.getEmail());

        @NotNull Cart cart = user.getCart();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product", productId));
        if (product.isNotAvailable()) {
            log.info("Product with ID " + productId + " should be available before adding to the cart for user " + user.getEmail());
            throw new UnavailableProductException("Product should be available before adding to the cart");
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
            itemToUpdate.setQuantity(quantity);
        }
        log.debug("Created cart item for user with email {}", user.getEmail());
        return cartMapper.mapToDto(cart); //TODO : change maybe return type
    }

    @Override
    public CartDto updateItemQuantity(Long productId, Integer updatedQuantity, @NotNull User user) {
        log.debug("Update item quantity by product ID {} and user email {}", productId, user.getEmail());

        @NotNull Cart cart = user.getCart();

        CartItem itemToUpdate = cart.getItemByProductId(productId);
        if (Objects.isNull(itemToUpdate)) {
            throw new EntityNotFoundException("Product with ID " + productId
                    + " not found in user cart with email " + cart.getUser().getEmail());
        }
        itemToUpdate.setQuantity(updatedQuantity);

        log.debug("Updated item quantity by product ID {} and user email {}", productId, user.getEmail());
        return cartMapper.mapToDto(cart); //TODO : change maybe return type
    }

    @Override
    public CartDto deleteItemFromCart(Long productId, @NotNull User user) {
        log.debug("Update item quantity by product ID {} and user email {}", productId, user.getEmail());

        @NotNull Cart cart = user.getCart();
        cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));

        log.debug("Updated item quantity by product ID {} and user email {}", productId, user.getEmail());
        return cartMapper.mapToDto(cart); //TODO : change maybe return type
    }
}
