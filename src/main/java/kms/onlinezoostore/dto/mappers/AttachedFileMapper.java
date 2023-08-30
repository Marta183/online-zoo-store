package kms.onlinezoostore.dto.mappers;

import kms.onlinezoostore.dto.AttachedFileDto;
import kms.onlinezoostore.entities.AttachedFile;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.SPRING)
public interface AttachedFileMapper {
    AttachedFileDto mapToDto(AttachedFile entity);
    AttachedFile mapToEntity(AttachedFileDto dto);
}
