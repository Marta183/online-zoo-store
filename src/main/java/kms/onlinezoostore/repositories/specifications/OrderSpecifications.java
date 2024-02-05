package kms.onlinezoostore.repositories.specifications;

import kms.onlinezoostore.entities.Order;
import org.springframework.data.jpa.domain.Specification;

public class OrderSpecifications {

    public static Specification<Order> findAllByQuickSearch(String searchValue) {
        return Specification.where(statusStartsWith(searchValue))
                .or(totalAmountLike(searchValue))
                .or(phoneNumberStartsWith(searchValue))
                .or(commentLike(searchValue))
                .or(userFirstNameOrLastNameStartsWithIgnoreCase(searchValue))
                ;
    }

//    public static Specification<Order> findAllByQuickSearchAndUser(String searchValue, ) {
//        return Specification.where(statusStartsWith(searchValue))
//                .or(totalAmountLike(searchValue))
//                .or(phoneNumberStartsWith(searchValue))
//                .or(commentLike(searchValue))
//                .and(userFirstNameOrLastNameStartsWithIgnoreCase(searchValue))
//                ;
//    }

    private static Specification<Order> statusStartsWith(String searchValue) {
        return (root, criteriaQuery, criteriaBuilder)
                -> criteriaBuilder.like(criteriaBuilder.upper(root.get("status")), searchValue.trim().toUpperCase() + "%");
    }

    private static Specification<Order> totalAmountLike(String searchValue) {
        return (root, criteriaQuery, criteriaBuilder)
                -> criteriaBuilder.like(root.get("totalAmount"), searchValue.trim() + "%");
    }

    private static Specification<Order> phoneNumberStartsWith(String searchValue) {
        return (root, criteriaQuery, criteriaBuilder)
                -> criteriaBuilder.like(root.get("phoneNumber"), searchValue.trim() + "%");
    }

    private static Specification<Order> commentLike(String searchValue) {
        return (root, criteriaQuery, criteriaBuilder)
                -> criteriaBuilder.like(criteriaBuilder.lower(root.get("comment")), "%" + searchValue.trim() + "%");
    }

    private static Specification<Order> userFirstNameOrLastNameStartsWithIgnoreCase(String searchValue) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("user").get("firstName")),
                                searchValue.toLowerCase() + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("user").get("lastName")),
                                searchValue.toLowerCase() + "%")
                );
    }
}
