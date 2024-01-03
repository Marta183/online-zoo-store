package kms.onlinezoostore.dto.mappers;

import kms.onlinezoostore.dto.user.UserResponse;
import kms.onlinezoostore.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.SPRING)
public interface UserResponseMapper {
    @Mapping(target = "countCartItems", expression = "java(entity.getCart() == null ? 0 : entity.getCart().getItems().size())")
    UserResponse mapToDto(User entity);
}
