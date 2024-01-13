package kms.onlinezoostore.dto.mappers;

import kms.onlinezoostore.dto.user.UserResponse;
import kms.onlinezoostore.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.SPRING)
public interface UserResponseMapper {
    UserResponse mapToDto(User entity);
}
