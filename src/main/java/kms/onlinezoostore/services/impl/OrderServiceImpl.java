package kms.onlinezoostore.services.impl;

import io.jsonwebtoken.lang.Strings;
import kms.onlinezoostore.dto.AddressDto;
import kms.onlinezoostore.dto.OrderDto;
import kms.onlinezoostore.dto.OrderItemDto;
import kms.onlinezoostore.dto.ProductDto;
import kms.onlinezoostore.dto.mappers.OrderMapper;
import kms.onlinezoostore.entities.AttachedFile;
import kms.onlinezoostore.entities.Order;
import kms.onlinezoostore.entities.Product;
import kms.onlinezoostore.entities.User;
import kms.onlinezoostore.entities.enums.DeliveryMethod;
import kms.onlinezoostore.entities.enums.OrderStatus;
import kms.onlinezoostore.exceptions.*;
import kms.onlinezoostore.repositories.OrderRepository;
import kms.onlinezoostore.repositories.ProductRepository;
import kms.onlinezoostore.repositories.specifications.OrderSpecifications;
import kms.onlinezoostore.services.OrderService;
import kms.onlinezoostore.services.ProductService;
import kms.onlinezoostore.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
//    private final ProductService productService;
    private final ProductRepository productRepository;
    private final UserService userService;
    private static final String ENTITY_CLASS_NAME = "ORDER";

    @Override
    public OrderDto findById(Long id) {
        log.debug("Finding {} by ID {}", ENTITY_CLASS_NAME, id);

        OrderDto orderDto = orderRepository.findById(id)
                .map(orderMapper::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));

        log.debug("Found {} by ID {}", ENTITY_CLASS_NAME, id);
        return orderDto;
    }

    @Override
    public OrderDto findByIdAndUser(Long id, User user) {
        log.debug("Finding {} by ID {} for user {}", ENTITY_CLASS_NAME, id, user.getEmail());

        OrderDto orderDto = orderRepository.findByIdAndUser(id, user)
                .map(orderMapper::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));

        log.debug("Found {} by ID {} for user {}", ENTITY_CLASS_NAME, id, user.getEmail());
        return orderDto;
    }

    @Override
    public Page<OrderDto> findPage(Pageable pageable) {
        log.debug("Finding {} page ", ENTITY_CLASS_NAME);
        log.debug("  with page number: {}, page size: {}", pageable.getPageNumber(), pageable.getPageSize());

        Page<OrderDto> page = orderRepository.findAll(pageable).map(orderMapper::mapToDto);

        log.debug("Found {} page with number of elements: {}", ENTITY_CLASS_NAME, page.getContent().size());
        return page;
    }

    @Override
    public Page<OrderDto> findPageByUser(User user, Pageable pageable) {
        log.debug("Finding {} page for user {}", ENTITY_CLASS_NAME, user.getEmail());
        log.debug("  with page number: {}, page size: {}", pageable.getPageNumber(), pageable.getPageSize());

        Page<OrderDto> page = orderRepository.findAllByUser(user, pageable).map(orderMapper::mapToDto);

        log.debug("Found {} page for user {} with number of elements: {}",
                ENTITY_CLASS_NAME, user.getEmail(), page.getContent().size());
        return page;
    }

    @Override
    public Page<OrderDto> findPageBySearchValue(String searchValue, Pageable pageable) {
        log.debug("Finding {} page by search value {}", ENTITY_CLASS_NAME, searchValue);
        log.debug("  with page number: {}, page size: {}", pageable.getPageNumber(), pageable.getPageSize());

        Page<OrderDto> page = orderRepository
                .findAll(OrderSpecifications.findAllByQuickSearch(searchValue), pageable)
                .map(orderMapper::mapToDto);

        log.debug("Found {} page by search value {} with number of elements: {}",
                searchValue, ENTITY_CLASS_NAME, page.getContent().size());
        return page;
    }

    @Override
    public Page<OrderDto> findActiveOrdersPageByUser(User user, Pageable pageable) {
        log.debug("Finding {} active orders page for user {}", ENTITY_CLASS_NAME, user.getEmail());
        log.debug("  with page number: {}, page size: {}", pageable.getPageNumber(), pageable.getPageSize());

//        Page<OrderDto> page = orderRepository
//                .findAllByStatusOrTotalAmountOrPhoneNumberOrCommentContainsIgnoreCaseAndUser(
//                        searchValue, user, pageable).map(orderMapper::mapToDto);

        Page<OrderDto> page = orderRepository
                .findAllByStatusInAndUser(
                        List.of(OrderStatus.NEW, OrderStatus.IN_PROCESS, OrderStatus.SHIPPED),
                        user, pageable)
                .map(orderMapper::mapToDto);

        log.debug("Found {} active orders page for user {} with number of elements: {}", ENTITY_CLASS_NAME,
                    user.getEmail(), page.getContent().size());
        return page;
    }

    @Override
    @Transactional
    public OrderDto createFromAdmin(OrderDto orderDto) {
        log.debug("Creating a new {} from admin", ENTITY_CLASS_NAME);

        Order savedOrder = processAndSaveOrder(orderDto);

        log.debug("New {} saved in DB with ID {}", ENTITY_CLASS_NAME, savedOrder.getId());
        return orderMapper.mapToDto(savedOrder);
    }

    @Override
    @Transactional
    public OrderDto createFromClient(OrderDto orderDto, User user) {
        log.debug("Creating a new {} from admin", ENTITY_CLASS_NAME);

        verifyUser(orderDto, user);

        // clear user's shopping cart // TODO save cart in db ?
        user.getCart().setItems(new ArrayList<>());

        Order savedOrder = processAndSaveOrder(orderDto);

        log.debug("New {} saved in DB with ID {}", ENTITY_CLASS_NAME, savedOrder.getId());
        return orderMapper.mapToDto(savedOrder);
    }

    private Order processAndSaveOrder(OrderDto orderDto) {
        verifyOrderData(orderDto);

        // saving entity
        Order order = orderMapper.mapToEntity(orderDto);
        order.setStatus(OrderStatus.NEW);
        order.setCompletedAt(null);
        order.setDeliveryNumber(null);
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public void update(Long id, OrderDto updatedOrderDto) {
        log.debug("Updating {} with ID {}", ENTITY_CLASS_NAME, id);

        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));

        // verification
        if (existingOrder.getStatus() == OrderStatus.COMPLETED) {
            throw new EntityCannotBeUpdated("Forbidden to change completed order.");
        }
        if (!updatedOrderDto.getId().equals(id)) {
            throw new EntityCannotBeUpdated("Order ID int the body doesn't match with order ID in the path.");
        }
        if (updatedOrderDto.getStatus() == OrderStatus.SHIPPED
                && !Strings.hasText(updatedOrderDto.getDeliveryNumber())) {
            throw new EntityCannotBeUpdated("Shipped order must have filled delivery number.");
        }
        verifyOrderData(updatedOrderDto);

        // saving entity
        Order orderToUpdate = orderMapper.mapToEntity(updatedOrderDto);
        orderToUpdate.setId(id);
        orderToUpdate.setCreatedAt(existingOrder.getCreatedAt());
        if (orderToUpdate.getStatus() == OrderStatus.COMPLETED) {
            orderToUpdate.setCompletedAt(LocalDateTime.now());
        } else {
            orderToUpdate.setStatus(existingOrder.getStatus());
        }
        orderRepository.save(orderToUpdate);

        log.debug("{} with ID {} updated in DB", ENTITY_CLASS_NAME, id);
    }

    @Override
    @Transactional
    public void cancelOrder(Long id, User user) {
        log.debug("Canceling {} with ID {}", ENTITY_CLASS_NAME, id);

        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));

        verifyUser(existingOrder, user);
        if (existingOrder.getStatus() != OrderStatus.NEW) {
            throw new EntityCannotBeUpdated("Clients allowed to change only new orders.");
        }
        existingOrder.setStatus(OrderStatus.CANCELED);
        orderRepository.save(existingOrder);

        log.debug("Canceled {} with ID {}", ENTITY_CLASS_NAME, id);
    }

    private void verifyOrderData(OrderDto orderDto) {
        verifyInnerEntities(orderDto);
        verifyProducts(orderDto.getItems());
        verifyTotalAmount(orderDto);
        verifyDeliveryAddress(orderDto.getDeliveryAddress(), orderDto.getDeliveryMethod());
    }

    private void verifyInnerEntities(OrderDto orderDto) {
        userService.findByEmail(orderDto.getUser().getEmail()); //TODO : change

//        orderDto.getItems().forEach(item ->
//                productService.findById(item.getProduct().getId())
//        );
    }

    private void verifyUser(OrderDto orderDto, User currentUser) {
        if (!orderDto.getUser().getId().equals(currentUser.getId())) {
            throw new BadCredentialsException("Authenticated user doesn't match with user in the order.");
        }
        userService.findByEmail(orderDto.getUser().getEmail());
    }

    private void verifyUser(Order order, User currentUser) {
        if (!order.getUser().getId().equals(currentUser.getId())) {
            throw new BadCredentialsException("Authenticated user doesn't match with user in the order.");
        }
    }

    private void verifyProducts(List<OrderItemDto> items) {
        if (items.isEmpty()) {
            throw new EmptyOrderException("Order must contain at least one item");
        }
        for (OrderItemDto item : items) {
            ProductDto productDto = item.getProduct();
            Product existingProduct = productRepository.findById(productDto.getId())
                    .orElseThrow(() -> new EntityNotFoundException("PRODUCT", productDto.getId()));

            if (existingProduct.isNotAvailable()) {
                throw new UnavailableProductException("Product with ID " + productDto.getId() + " is not available.");
            }
            if (item.getPrice() != existingProduct.getCurrentPrice()) {
                throw new PriceConflictException(""); // TODO: add msg for ex
            }
            double requiredAmount = item.getQuantity() * existingProduct.getCurrentPrice();
            if (item.getAmount() != requiredAmount) {
                throw new PriceConflictException(""); // TODO: add msg for ex
            }
        }
//        long unavailableProductsCount = items.stream()
//                .filter(item -> item.getProduct().isNotAvailable())
//                .count();
//        if (unavailableProductsCount > 0) {
//            throw new UnavailableProductException("There are " + unavailableProductsCount + " unavailable products in the order");
//        }
    }

    private void verifyTotalAmount(OrderDto orderDto) {
        double correctTotalAmount = orderDto.calculateTotalAmount();
        if (correctTotalAmount != orderDto.getTotalAmount()) {
            throw new InvalidTotalAmountException(
                    "Correct value: " + correctTotalAmount + ", received value: " + orderDto.getTotalAmount());
        }
    }

    private void verifyDeliveryAddress(AddressDto addressDto, DeliveryMethod deliveryMethod) {
        boolean isCourierAddressCorrect =
                !Strings.hasText(addressDto.getPostalDepartmentNumber())
                && Strings.hasText(addressDto.getStreet())
                && Strings.hasText(addressDto.getHouseNumber())
                && Strings.hasText(addressDto.getApartment());

        boolean isPostOfficeAddressCorrect =
                Strings.hasText(addressDto.getPostalDepartmentNumber())
                && !Strings.hasText(addressDto.getStreet())
                && !Strings.hasText(addressDto.getHouseNumber())
                && !Strings.hasText(addressDto.getApartment());

        if (deliveryMethod == DeliveryMethod.POST_OFFICE && !isPostOfficeAddressCorrect) {
            throw new InvalidAddressException("Incorrect address: Fulfill postal department number without personal address.");
        } else if (deliveryMethod == DeliveryMethod.COURIER && !isCourierAddressCorrect) {
            throw new InvalidAddressException("Incorrect address: Fulfill personal address without postal department number.");
        }
    }
}
