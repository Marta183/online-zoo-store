package kms.onlinezoostore.dto.mappers;

import kms.onlinezoostore.dto.ColorDto;
import kms.onlinezoostore.entities.Color;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ColorMapper {
    ColorMapper INSTANCE = Mappers.getMapper(ColorMapper.class);

    ColorDto mapToDto(Color entity);
    Color mapToEntity(ColorDto dto);
}
