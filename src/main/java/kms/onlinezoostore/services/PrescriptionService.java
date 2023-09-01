package kms.onlinezoostore.services;

import kms.onlinezoostore.dto.PrescriptionDto;

import java.util.List;

public interface PrescriptionService {

    PrescriptionDto findById(Long id);
    List<PrescriptionDto> findAll();

    PrescriptionDto create(PrescriptionDto brandDto);
    void update(Long id, PrescriptionDto brandDto);
    void deleteById(Long id);
}
