package kms.onlinezoostore.dto.mappers;

import kms.onlinezoostore.dto.user.UserResponseDto;
import kms.onlinezoostore.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.SPRING)
public interface UserResponseMapper {
    UserResponseDto mapToDto(User entity);
    User mapToEntity(UserResponseDto dto);
}
