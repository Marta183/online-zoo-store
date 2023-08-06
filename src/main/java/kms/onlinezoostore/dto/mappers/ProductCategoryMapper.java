package kms.onlinezoostore.dto.mappers;

import kms.onlinezoostore.dto.ProductCategoryDto;
import kms.onlinezoostore.entities.ProductCategory;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductCategoryMapper {
    ProductCategoryMapper INSTANCE = Mappers.getMapper(ProductCategoryMapper.class);

    ProductCategoryDto mapToDto(ProductCategory entity);
    ProductCategory mapToEntity(ProductCategoryDto dto);
}
