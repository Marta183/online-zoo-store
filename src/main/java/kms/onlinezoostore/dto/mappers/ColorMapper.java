package kms.onlinezoostore.dto.mappers;

import kms.onlinezoostore.dto.ColorDto;
import kms.onlinezoostore.entities.Color;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ColorMapper {
    ColorDto mapToDto(Color entity);
    Color mapToEntity(ColorDto dto);
}
