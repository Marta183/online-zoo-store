package kms.onlinezoostore.dto.mappers;

import kms.onlinezoostore.dto.AttachedFileDto;
import kms.onlinezoostore.dto.ConstantDto;
import kms.onlinezoostore.entities.AttachedFile;
import kms.onlinezoostore.entities.Constant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ConstantMapper {

    @Mapping(target = "value", expression = "java(mapToDto_value(entity))")
    ConstantDto mapToDto(Constant entity);

    default Object mapToDto_value(Constant entity) {
        if (entity.isAttachedFile())
            return mapToDto_imageListToValue(entity.getImages());
        return entity.getValue();
    }

    default AttachedFileDto mapToDto_imageListToValue(List<AttachedFile> images) {
        if(images == null || images.isEmpty())
            return null;
        return AttachedFileMapper.INSTANCE.mapToDto(images.get(0));
    }
}
