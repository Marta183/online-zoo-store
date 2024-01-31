package kms.onlinezoostore.repositories;

import kms.onlinezoostore.entities.CartItem;
import kms.onlinezoostore.entities.WishListItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WishListItemRepository extends CrudRepository<WishListItem, Long> {
    Optional<CartItem> findByProductId(Long productId);
}
