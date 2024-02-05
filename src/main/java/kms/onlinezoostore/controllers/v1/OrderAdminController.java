package kms.onlinezoostore.controllers.v1;

import io.jsonwebtoken.lang.Strings;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kms.onlinezoostore.config.springdoc.PageableAsQueryParam;
import kms.onlinezoostore.dto.OrderDto;
import kms.onlinezoostore.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@Tag(name = "Orders (admin)")
@RequiredArgsConstructor
@RequestMapping(value = OrderAdminController.REST_URL)
public class OrderAdminController {
    static final String REST_URL = "/api/v1/admin/orders";
    private final OrderService orderService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get order by ID", description = "Get order details by ID")
    public OrderDto findById(@PathVariable Long id) {
        return orderService.findById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PageableAsQueryParam
    @Operation(summary = "Get all orders", description = "Get a list of all orders")
    public Page<OrderDto> findPage(@Parameter(required = false) String searchValue,
                                   @Parameter(hidden = true) Pageable pageable) {
        return (Strings.hasText(searchValue)) ?
                orderService.findPageBySearchValue(searchValue, pageable) :
                orderService.findPage(pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new order", description = "Create a new order with the provided details")
    public OrderDto create(@RequestBody @Valid OrderDto orderDto) {
        return orderService.createFromAdmin(orderDto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update order by ID", description = "Update an existing order by ID")
    public void update(@PathVariable Long id, @RequestBody @Valid OrderDto orderDto) {
        orderService.update(id, orderDto);
    }
}
