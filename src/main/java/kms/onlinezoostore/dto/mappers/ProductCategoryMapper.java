package kms.onlinezoostore.dto.mappers;

import kms.onlinezoostore.dto.AttachedFileDto;
import kms.onlinezoostore.dto.ProductCategoryDto;
import kms.onlinezoostore.entities.AttachedFile;
import kms.onlinezoostore.entities.ProductCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductCategoryMapper {
    @Mapping(target = "image", source = "images", qualifiedByName = "imageListToImageDto")
    ProductCategoryDto mapToDto(ProductCategory entity);

    @Mapping(target = "images", source = "image", qualifiedByName = "imageDtoToImageList")
    ProductCategory mapToEntity(ProductCategoryDto dto);

    @Named("imageListToImageDto")
    default AttachedFileDto imageListToImageDto(List<AttachedFile> images) {
        if(images == null || images.isEmpty())
            return null;
        return AttachedFileMapper.INSTANCE.mapToDto(images.get(0));
    }

    @Named("imageDtoToImageList")
    default List<AttachedFile> imageDtoToImageList(AttachedFileDto imageDto) {
        if (imageDto == null) {
            Collections.emptyList();
        }
        return Collections.singletonList(AttachedFileMapper.INSTANCE.mapToEntity(imageDto));
    }
}
