package kms.onlinezoostore.dto.mappers;

import kms.onlinezoostore.dto.CartItemDto;
import kms.onlinezoostore.entities.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CartItemMapper {
    @Mapping(target = "price", defaultExpression = "getPrice()")
    @Mapping(target = "sum", defaultExpression = "getSum()")
    CartItemDto mapToDto(CartItem entity);

    CartItem mapToEntity(CartItemDto dto);
}
