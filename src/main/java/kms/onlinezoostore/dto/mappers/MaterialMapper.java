package kms.onlinezoostore.dto.mappers;

import kms.onlinezoostore.dto.MaterialDto;
import kms.onlinezoostore.entities.Material;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MaterialMapper {
    MaterialMapper INSTANCE = Mappers.getMapper(MaterialMapper.class);

    MaterialDto mapToDto(Material entity);
    Material mapToEntity(MaterialDto dto);
}
