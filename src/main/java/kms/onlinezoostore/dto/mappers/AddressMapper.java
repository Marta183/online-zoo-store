package kms.onlinezoostore.dto.mappers;

import kms.onlinezoostore.dto.AddressDto;
import kms.onlinezoostore.entities.Address;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.SPRING)
public interface AddressMapper {
    AddressDto mapToDto(Address entity);
    Address mapToEntity(AddressDto dto);
}
