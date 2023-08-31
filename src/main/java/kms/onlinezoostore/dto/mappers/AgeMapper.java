package kms.onlinezoostore.dto.mappers;

import kms.onlinezoostore.dto.AgeDto;
import kms.onlinezoostore.entities.Age;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.SPRING)
public interface AgeMapper {
    AgeDto mapToDto(Age entity);
    Age mapToEntity(AgeDto dto);
}
