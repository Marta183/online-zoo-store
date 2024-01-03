package kms.onlinezoostore.dto.mappers;

import kms.onlinezoostore.dto.CartDto;
import kms.onlinezoostore.entities.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CartMapper {
    @Mapping(target = "totalPrice", expression = "java(entity.getTotalPrice())")
    @Mapping(target = "userEmail", expression = "java(entity.getUser().getEmail())")
    CartDto mapToDto(Cart entity);
}
