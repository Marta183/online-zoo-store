package kms.onlinezoostore.dto.mappers;

import kms.onlinezoostore.dto.AttachedFileDto;
import kms.onlinezoostore.entities.AttachedFile;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.SPRING)
public interface AttachedFileMapper {
    AttachedFileDto mapToDto(AttachedFile entity);
    AttachedFile mapToEntity(AttachedFileDto dto);

//    @Mapping(target = "owner.id", source = "entity.ownerId")
//    @Mapping(target = "owner.ownerClassName", source = "entity.ownerClass")
//    AttachedFileDto mapToDtoWithOwner(AttachedFile entity);

//    @Mapping(target = "ownerId", source = "dto.owner.id")
//    @Mapping(target = "ownerClass", source = "dto.owner.ownerClassName")
//    AttachedFile toEntityWithOwner(AttachedFileDto dto);
}
