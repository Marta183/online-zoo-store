package kms.onlinezoostore.services;

import kms.onlinezoostore.dto.ColorDto;

import java.util.List;

public interface ColorService {

    ColorDto findById(Long id);
    List<ColorDto> findAll();

    ColorDto create(ColorDto colorDto);
    void update(Long id, ColorDto colorDto);
    void deleteById(Long id);
}
