package kms.onlinezoostore.repositories;

import kms.onlinezoostore.entities.Order;
import kms.onlinezoostore.entities.User;
import kms.onlinezoostore.entities.enums.OrderStatus;
import kms.onlinezoostore.services.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    Optional<Order> findByIdAndUser(Long id, User user);

    Page<Order> findAllByUser(User user, Pageable pageable);

//    Page<Order> findAllByStatusOrTotalAmountOrPhoneNumberOrCommentContainsIgnoreCaseOrUser(
//            String searchValue,
//            Pageable pageable);
////    Page<Order> findAllByStatusOrTotalAmountOrPhoneNumberOrCommentContainsIgnoreCaseAndUser(
////            String searchValue,
////            User user,
////            Pageable pageable);
//
////    @Query("FROM Order o WHERE o.user = :user " +
////            "AND (o.status = 'COMPLETED' AND o.status != 'CANCELED') ")
////    Page<Order> findActiveOrdersPageByUser(User user, Pageable pageable);
//
    Page<Order> findAllByStatusInAndUser(List<OrderStatus> activeStatuses, User user, Pageable pageable);
}
