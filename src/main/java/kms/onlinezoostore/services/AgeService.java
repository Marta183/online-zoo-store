package kms.onlinezoostore.services;

import kms.onlinezoostore.dto.AgeDto;

import java.util.List;

public interface AgeService {

    AgeDto findById(Long id);
    List<AgeDto> findAll();

    AgeDto create(AgeDto ageDto);
    void update(Long id, AgeDto ageDto);
    void deleteById(Long id);
}
