package kms.onlinezoostore.repositories;

import kms.onlinezoostore.entities.WishListItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WishListItemRepository extends CrudRepository<WishListItem, Long> {
}
