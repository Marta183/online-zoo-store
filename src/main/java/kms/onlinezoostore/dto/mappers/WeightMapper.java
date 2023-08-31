package kms.onlinezoostore.dto.mappers;

import kms.onlinezoostore.dto.WeightDto;
import kms.onlinezoostore.entities.Weight;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface WeightMapper {
    WeightDto mapToDto(Weight entity);
    Weight mapToEntity(WeightDto dto);
}
