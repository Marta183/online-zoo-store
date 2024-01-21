package kms.onlinezoostore.services;

import kms.onlinezoostore.dto.WishListDto;
import kms.onlinezoostore.entities.User;

import java.util.List;

public interface WishListService {

    WishListDto findByUser(User user);
    void createDefaultWishListForUser(User user);
    void addItem(Long productId, User user);
    void addItems(List<Long> productIds, User user);
    void deleteItem(Long productId, User user);
    void deleteAllItems(User user);
}
