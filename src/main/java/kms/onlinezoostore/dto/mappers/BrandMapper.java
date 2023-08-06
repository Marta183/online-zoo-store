package kms.onlinezoostore.dto.mappers;

import kms.onlinezoostore.dto.BrandDto;
import kms.onlinezoostore.entities.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BrandMapper {
    BrandMapper INSTANCE = Mappers.getMapper(BrandMapper.class);

    BrandDto mapToDto(Brand entity);
    Brand mapToEntity(BrandDto dto);
}
