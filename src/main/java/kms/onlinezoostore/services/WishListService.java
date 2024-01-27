package kms.onlinezoostore.services;

import kms.onlinezoostore.dto.WishListDto;
import kms.onlinezoostore.entities.User;

import java.util.Set;

public interface WishListService {

    WishListDto findByUser(User user);
    void createDefaultWishListForUser(User user);

    void refillWishList(Set<Long> productIds, User user);
    void addItems(Set<Long> productIds, User user);

    void deleteItem(Long productId, User user);
    void deleteItems(Set<Long> productIds, User user);
    void deleteAllItems(User user);
}
