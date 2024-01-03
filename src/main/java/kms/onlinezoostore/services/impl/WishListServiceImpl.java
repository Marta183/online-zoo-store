package kms.onlinezoostore.services.impl;

import jakarta.validation.constraints.NotNull;
import kms.onlinezoostore.dto.WishListDto;
import kms.onlinezoostore.dto.mappers.WishListMapper;
import kms.onlinezoostore.entities.WishList;
import kms.onlinezoostore.entities.WishListItem;
import kms.onlinezoostore.entities.Product;
import kms.onlinezoostore.entities.User;
import kms.onlinezoostore.exceptions.EntityDuplicateException;
import kms.onlinezoostore.exceptions.EntityNotFoundException;
import kms.onlinezoostore.repositories.ProductRepository;
import kms.onlinezoostore.repositories.WishListItemRepository;
import kms.onlinezoostore.repositories.WishListRepository;
import kms.onlinezoostore.services.WishListService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class WishListServiceImpl implements WishListService {
    private final WishListMapper wishListMapper;
    private final WishListRepository wishListRepository;
    private final WishListItemRepository wishListItemRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public WishListDto findByUser(@NotNull User user) {
        log.debug("Finding wish list by user with email {}", user.getEmail());

        WishListDto wishListDto = wishListMapper.mapToDto(user.getWishList());

        log.debug("Found wish list by user with email {}", user.getEmail());
        return wishListDto;
    }

    @Override
    public void createDefaultWishListForUser(@NotNull User user) {
        String userEmail = user.getEmail();
        log.debug("Creating wish list for user with email {}", userEmail);

        WishList existingWishList = user.getWishList();
        if (Objects.nonNull(existingWishList)) {
            throw new EntityDuplicateException("Default wish list already exists for user email= " + userEmail);
        }
        WishList wishList = new WishList();
        wishList.setUser(user);
        wishList.setName("Default");
        WishList savedWishList = wishListRepository.save(wishList);
        user.setWishList(savedWishList);

        log.debug("Created wish list for user with email {}", userEmail);
    }

    @Override
    public void addItem(Long productId, @NotNull User user) {
        log.debug("Create WishListItem for user with email {}", user.getEmail());

        WishList wishList = user.getWishList();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product", productId));

        WishListItem existsItem = wishList.getItemByProductId(productId);
        if (Objects.isNull(existsItem)) {
            WishListItem newItem = new WishListItem();
            newItem.setWishList(wishList);
            newItem.setProduct(product);
            WishListItem savedItem = wishListItemRepository.save(newItem);

            wishList.getItems().add(savedItem);
        } else {
            log.debug("Item with product id {} already exists in the wish list with user email {}", productId, user.getEmail());
            throw new EntityDuplicateException("Product already exists in the wish list");
        }
        log.debug("Created WishListItem for user with email {}", user.getEmail());
    }

    @Override
    public void deleteItem(Long productId, @NotNull User user) {
        log.debug("Deleting WishListItem by product ID {} and user email {}", productId, user.getEmail());

        WishList wishList = user.getWishList();
        wishList.getItems().removeIf(item -> item.getProduct().getId().equals(productId));
        wishListRepository.save(wishList);

        log.debug("Deleted WishListItem by product ID {} and user email {}", productId, user.getEmail());
    }

    @Override
    public void deleteAllItems(@NotNull User user) {
        log.debug("Clear WishList for user with email {}", user.getEmail());

        WishList wishList = user.getWishList();
        wishList.setItems(new HashSet<>());
        wishListRepository.save(wishList);

        log.debug("Cleared WishList for user with email {}", user.getEmail());
    }
}
