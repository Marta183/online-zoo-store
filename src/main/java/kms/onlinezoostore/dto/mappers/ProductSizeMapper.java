package kms.onlinezoostore.dto.mappers;

import kms.onlinezoostore.dto.ProductSizeDto;
import kms.onlinezoostore.entities.ProductSize;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductSizeMapper {
    ProductSizeMapper INSTANCE = Mappers.getMapper(ProductSizeMapper.class);

    ProductSizeDto mapToDto(ProductSize entity);
    ProductSize mapToEntity(ProductSizeDto dto);
}
