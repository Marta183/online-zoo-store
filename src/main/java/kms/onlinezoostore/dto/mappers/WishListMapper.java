package kms.onlinezoostore.dto.mappers;

import kms.onlinezoostore.dto.ProductDto;
import kms.onlinezoostore.dto.WishListDto;
import kms.onlinezoostore.entities.WishList;
import kms.onlinezoostore.entities.WishListItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface WishListMapper {
    WishListMapper INSTANCE = Mappers.getMapper(WishListMapper.class);

    @Mapping(target = "userEmail", expression = "java(entity.getUser().getEmail())")
    @Mapping(target = "products", source = "items", qualifiedByName = "itemsToProductsDto")
    WishListDto mapToDto(WishList entity);

    @Named("itemsToProductsDto")
    default Set<ProductDto> itemsToProductsDto(Set<WishListItem> items) {
        return items.stream()
                .map(WishListItem::getProduct)
                .map(ProductMapper.INSTANCE::mapToDto)
                .collect(Collectors.toSet());
    }
}
