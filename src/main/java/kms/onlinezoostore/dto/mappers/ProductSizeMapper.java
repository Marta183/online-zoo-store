package kms.onlinezoostore.dto.mappers;

import kms.onlinezoostore.dto.ProductSizeDto;
import kms.onlinezoostore.entities.ProductSize;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductSizeMapper {
    ProductSizeDto mapToDto(ProductSize entity);
    ProductSize mapToEntity(ProductSizeDto dto);
}
