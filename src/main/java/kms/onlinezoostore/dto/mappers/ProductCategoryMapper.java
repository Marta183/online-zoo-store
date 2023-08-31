package kms.onlinezoostore.dto.mappers;

import kms.onlinezoostore.dto.ProductCategoryDto;
import kms.onlinezoostore.entities.ProductCategory;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductCategoryMapper {
    ProductCategoryDto mapToDto(ProductCategory entity);
    ProductCategory mapToEntity(ProductCategoryDto dto);
}
