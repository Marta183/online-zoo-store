package kms.onlinezoostore.dto.mappers;

import kms.onlinezoostore.dto.CartItemDto;
import kms.onlinezoostore.entities.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CartItemMapper {
    @Mapping(target = "price", expression = "java(entity.getPrice())")
    @Mapping(target = "sum", expression = "java(entity.getSum())")
    CartItemDto mapToDto(CartItem entity);
}
