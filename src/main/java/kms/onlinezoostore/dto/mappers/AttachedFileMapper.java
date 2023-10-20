package kms.onlinezoostore.dto.mappers;

import kms.onlinezoostore.dto.AttachedFileDto;
import kms.onlinezoostore.entities.AttachedFile;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = ComponentModel.SPRING)
public interface AttachedFileMapper {
    AttachedFileMapper INSTANCE = Mappers.getMapper(AttachedFileMapper.class);

    AttachedFileDto mapToDto(AttachedFile entity);
    AttachedFile mapToEntity(AttachedFileDto dto);
}
