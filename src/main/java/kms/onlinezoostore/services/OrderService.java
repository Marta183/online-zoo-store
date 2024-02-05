package kms.onlinezoostore.services;

import kms.onlinezoostore.dto.OrderDto;
import kms.onlinezoostore.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderDto findById(Long id);
    OrderDto findByIdAndUser(Long id, User user);

    Page<OrderDto> findPage(Pageable pageable);
    Page<OrderDto> findPageByUser(User user, Pageable pageable);
    Page<OrderDto> findPageBySearchValue(String quickSearch, Pageable pageable);
//    Page<OrderDto> findPageBySearchValueAndUser(String quickSearch, User user, Pageable pageable);
    Page<OrderDto> findActiveOrdersPageByUser(User user, Pageable pageable);

    OrderDto createFromAdmin(OrderDto orderDto);
    OrderDto createFromClient(OrderDto orderDto, User user);

    void update(Long id, OrderDto orderDto);

    void cancelOrder(Long id, User user);
}
