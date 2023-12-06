package kms.onlinezoostore.dto.mappers;

import kms.onlinezoostore.dto.user.UserCreateRequestDto;
import kms.onlinezoostore.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.SPRING)
public interface UserCreateRequestMapper {
    UserCreateRequestDto mapToDto(User entity);
    User mapToEntity(UserCreateRequestDto dto);
}
