package kms.onlinezoostore.dto.mappers;

import kms.onlinezoostore.dto.PrescriptionDto;
import kms.onlinezoostore.entities.Prescription;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PrescriptionMapper {
    PrescriptionDto mapToDto(Prescription entity);
    Prescription mapToEntity(PrescriptionDto dto);
}
