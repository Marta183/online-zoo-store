package kms.onlinezoostore.dto.mappers;

import kms.onlinezoostore.dto.ProductDto;
import kms.onlinezoostore.entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMapper {
    ProductDto mapToDto(Product entity);
    Product mapToEntity(ProductDto dto);
}
