package kms.onlinezoostore.services;

import kms.onlinezoostore.dto.WishListDto;
import kms.onlinezoostore.entities.User;

public interface WishListService {

    WishListDto findByUser(User user);
    void createDefaultWishListForUser(User user);
    void addItem(Long productId, User user);
    void deleteItem(Long productId, User user);
    void deleteAllItems(User user);
}
