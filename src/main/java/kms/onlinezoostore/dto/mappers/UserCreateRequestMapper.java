package kms.onlinezoostore.dto.mappers;

import kms.onlinezoostore.dto.user.UserCreateRequest;
import kms.onlinezoostore.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.SPRING)
public interface UserCreateRequestMapper {
    User mapToEntity(UserCreateRequest dto);
}
