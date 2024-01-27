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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;
import java.util.stream.Collectors;

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
    public void refillWishList(Set<Long> newProductIds, User user) {
        log.debug("Refilling wish list for user with email {} with new {} products", user.getEmail(), newProductIds.size());

        Set<WishListItem> wishListItems = user.getWishList().getItems();
        Set<Long> presentProductsId = wishListItems.stream()
                .map(item -> item.getProduct().getId())
                .collect(Collectors.toSet());

        if (presentProductsId.containsAll(newProductIds) && newProductIds.containsAll(presentProductsId)) {
            log.debug("Canceled refilling wish list for user with email {}: all elements are the same", user.getEmail());
            return;
        }

        presentProductsId.removeAll(newProductIds); // retain all elements to delete

        deleteItems(presentProductsId, user);
        addItems(newProductIds, user);

        log.debug("Refilled wish list for user with email {}", user.getEmail());
    }

    @Override
    public void addItems(Set<Long> productIds, User user) {
        log.debug("Adding {} products to wishList for user with email {}", productIds.size(), user.getEmail());

        WishList wishList = user.getWishList();

        Set<WishListItem> newItemsToSave = new HashSet<>();
        for (Long productId : productIds) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new EntityNotFoundException("Product", productId));

            WishListItem existsItem = wishList.getItemByProductId(productId);
            if (Objects.isNull(existsItem)) {
                WishListItem newItem = new WishListItem();
                newItem.setWishList(wishList);
                newItem.setProduct(product);
                newItemsToSave.add(newItem);
            }
        }
        if (!newItemsToSave.isEmpty()) {
            wishList.getItems().addAll(
                    (Collection<WishListItem>) wishListItemRepository.saveAll(newItemsToSave)
            );
            wishListRepository.save(wishList);
        }

        log.debug("Added {} products to wishList for user with email {}", productIds.size(), user.getEmail());
    }

    @Override
    public void deleteItem(Long productId, @NotNull User user) {
        log.debug("Deleting from WishListItem by product ID {} and user email {}", productId, user.getEmail());

        WishList wishList = user.getWishList();
        wishList.getItems().removeIf(item -> item.getProduct().getId().equals(productId));
        wishListRepository.save(wishList);

        log.debug("Deleted from WishListItem by product ID {} and user email {}", productId, user.getEmail());
    }

    @Override
    public void deleteItems(Set<Long> productIds, User user) {
        log.debug("Deleting from WishListItem {} products by user email {}", productIds.size(), user.getEmail());

        WishList wishList = user.getWishList();
        for (Long productId : productIds) {
            wishList.getItems().removeIf(item -> item.getProduct().getId().equals(productId));
        }
        wishListRepository.save(wishList);

        log.debug("Deleted from WishListItem by product ID {} and user email {}", productIds.size(), user.getEmail());
    }

    @Override
    public void deleteAllItems(@NotNull User user) {
        log.debug("Clear WishList for user with email {}", user.getEmail());

        WishList wishList = user.getWishList();
        wishList.getItems().clear();
        wishListRepository.save(wishList);

        log.debug("Cleared WishList for user with email {}", user.getEmail());
    }
}
