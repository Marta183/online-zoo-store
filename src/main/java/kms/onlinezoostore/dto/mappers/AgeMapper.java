package kms.onlinezoostore.dto.mappers;

import kms.onlinezoostore.dto.AgeDto;
import kms.onlinezoostore.entities.Age;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = ComponentModel.SPRING)
public interface AgeMapper {
    AgeMapper INSTANCE = Mappers.getMapper(AgeMapper.class);

    AgeDto mapToDto(Age entity);
    Age mapToEntity(AgeDto dto);
}
