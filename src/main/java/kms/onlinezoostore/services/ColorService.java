package kms.onlinezoostore.services;

import kms.onlinezoostore.dto.ColorDto;
import kms.onlinezoostore.services.files.images.SingleImageOwnerService;

import java.util.List;

public interface ColorService  extends SingleImageOwnerService {

    ColorDto findById(Long id);
    List<ColorDto> findAll();

    ColorDto create(ColorDto colorDto);
    void update(Long id, ColorDto colorDto);
    void deleteById(Long id);
}
