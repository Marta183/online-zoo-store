package kms.onlinezoostore.controllers.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kms.onlinezoostore.config.springdoc.PageableAsQueryParam;
import kms.onlinezoostore.dto.OrderDto;
import kms.onlinezoostore.services.OrderService;
import kms.onlinezoostore.utils.UsersUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@Tag(name = "Orders (clients)")
@RequiredArgsConstructor
@RequestMapping(value = OrderClientController.REST_URL)
public class OrderClientController {
    static final String REST_URL = "/api/v1/orders";
    private final OrderService orderService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get order by ID", description = "Get order details by ID for current user.")
    public OrderDto findById(@PathVariable Long id,
                             @AuthenticationPrincipal UserDetails userDetails) {
        return orderService.findByIdAndUser(id, UsersUtil.extractUser(userDetails));
    }

//    @GetMapping
//    @ResponseStatus(HttpStatus.OK)
//    @PageableAsQueryParam
//    @Operation(summary = "Get all orders", description = "Get a list of all orders")
//    public Page<OrderDto> findPage(@Parameter(required = false) String searchValue,
//                                   @Parameter(hidden = true) Pageable pageable,
//                                   @AuthenticationPrincipal UserDetails userDetails) {
//        if (searchValue != null && !searchValue.isBlank()) {
//            return orderService.findPageBySearchValueAndUser(searchValue,
//                    UsersUtil.extractUser(userDetails),
//                    pageable);
//        }
//        return orderService.findPageByUser(UsersUtil.extractUser(userDetails), pageable);
//    }

//    @GetMapping
//    @ResponseStatus(HttpStatus.OK)
//    @PageableAsQueryParam
//    @Operation(summary = "Get all orders", description = "Get a list of all orders")
//    public Page<OrderDto> findActiveOrderPage(@Parameter(hidden = true) Pageable pageable,
//                                              @AuthenticationPrincipal UserDetails userDetails) {
//        return orderService.findActiveOrdersPageByUser(UsersUtil.extractUser(userDetails), pageable);
//    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PageableAsQueryParam
    @Operation(summary = "Get all orders", description = "Get a list of all orders for current user")
    public Page<OrderDto> findPage(@Parameter(required = false) boolean onlyActive,
                                   @Parameter(hidden = true) Pageable pageable,
                                   @AuthenticationPrincipal UserDetails userDetails) {
        return (onlyActive) ?
                orderService.findActiveOrdersPageByUser(UsersUtil.extractUser(userDetails), pageable) :
                orderService.findPageByUser(UsersUtil.extractUser(userDetails), pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new order", description = "Create a new order with the provided details")
    public OrderDto create(@RequestBody @Valid OrderDto orderDto,
                           @AuthenticationPrincipal UserDetails userDetails) {
        return orderService.createFromClient(orderDto, UsersUtil.extractUser(userDetails));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Cancel order by ID", description = "Cancel an existing order by ID")
    public void cancel(@PathVariable Long id,
                       @AuthenticationPrincipal UserDetails userDetails) {
        orderService.cancelOrder(id, UsersUtil.extractUser(userDetails));
    }
}
