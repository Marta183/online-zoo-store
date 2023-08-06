package kms.onlinezoostore.services;

import kms.onlinezoostore.dto.MaterialDto;

import java.util.List;

public interface MaterialService {

    MaterialDto findById(Long id);
    List<MaterialDto> findAll();

    MaterialDto create(MaterialDto materialDto);
    void update(Long id, MaterialDto materialDto);
    void deleteById(Long id);
}
