package kms.onlinezoostore.repositories;

import kms.onlinezoostore.entities.WishList;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WishListRepository extends CrudRepository<WishList, Long> {
}
