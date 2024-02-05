package kms.onlinezoostore.dto.mappers;

import kms.onlinezoostore.dto.OrderItemDto;
import kms.onlinezoostore.entities.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.SPRING)
public interface OrderItemMapper {
    OrderItemDto mapToDto(OrderItem entity);
    OrderItem mapToEntity(OrderItemDto dto);
}
