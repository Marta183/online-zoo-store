package kms.onlinezoostore.dto.mappers;

import kms.onlinezoostore.dto.ProductDto;
import kms.onlinezoostore.entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    ProductDto mapToDto(Product entity);
    Product mapToEntity(ProductDto dto);
}
