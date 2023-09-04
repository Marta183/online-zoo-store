package kms.onlinezoostore.services;

import kms.onlinezoostore.dto.BrandDto;
import kms.onlinezoostore.services.files.images.SingleImageOwnerService;

import java.util.List;

public interface BrandService extends SingleImageOwnerService {

    BrandDto findById(Long id);
    List<BrandDto> findAll();

    BrandDto create(BrandDto brandDto);
    void update(Long id, BrandDto brandDto);
    void deleteById(Long id);
}
