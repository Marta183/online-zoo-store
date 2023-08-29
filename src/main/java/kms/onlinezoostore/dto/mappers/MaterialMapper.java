package kms.onlinezoostore.dto.mappers;

import kms.onlinezoostore.dto.MaterialDto;
import kms.onlinezoostore.entities.Material;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MaterialMapper {
    MaterialDto mapToDto(Material entity);
    Material mapToEntity(MaterialDto dto);
}
