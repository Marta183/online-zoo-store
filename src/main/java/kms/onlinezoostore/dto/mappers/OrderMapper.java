package kms.onlinezoostore.dto.mappers;

import kms.onlinezoostore.dto.OrderDto;
import kms.onlinezoostore.entities.Order;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.SPRING)
public interface OrderMapper {
    OrderDto mapToDto(Order entity);
    Order mapToEntity(OrderDto dto);
}
