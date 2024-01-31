package kms.onlinezoostore.repositories;

import kms.onlinezoostore.entities.CartItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends CrudRepository<CartItem, Long> {
    Optional<CartItem> findByProductId(Long productId);
}
