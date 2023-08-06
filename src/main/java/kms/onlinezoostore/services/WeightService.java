package kms.onlinezoostore.services;

import kms.onlinezoostore.dto.WeightDto;

import java.util.List;

public interface WeightService {

    WeightDto findById(Long id);
    List<WeightDto> findAll();

    WeightDto create(WeightDto weightDto);
    void update(Long id, WeightDto weightDto);
    void deleteById(Long id);
}
