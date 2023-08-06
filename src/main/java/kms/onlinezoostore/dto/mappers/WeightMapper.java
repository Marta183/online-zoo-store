package kms.onlinezoostore.dto.mappers;

import kms.onlinezoostore.dto.WeightDto;
import kms.onlinezoostore.entities.Weight;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface WeightMapper {
    WeightMapper INSTANCE = Mappers.getMapper(WeightMapper.class);

    WeightDto mapToDto(Weight entity);
    Weight mapToEntity(WeightDto dto);
}
