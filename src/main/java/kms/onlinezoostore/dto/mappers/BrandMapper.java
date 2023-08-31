package kms.onlinezoostore.dto.mappers;

import kms.onlinezoostore.dto.BrandDto;
import kms.onlinezoostore.entities.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BrandMapper {
    BrandDto mapToDto(Brand entity);
    Brand mapToEntity(BrandDto dto);
}
