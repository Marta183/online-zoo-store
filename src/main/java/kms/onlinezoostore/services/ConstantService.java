package kms.onlinezoostore.services;

import kms.onlinezoostore.dto.ConstantDto;
import kms.onlinezoostore.entities.enums.ConstantKeys;

import java.util.List;

public interface ConstantService {

    List<ConstantDto> findAll();

    ConstantDto findByKey(ConstantKeys key);

    Object updateValue(ConstantKeys key, Object updatedValue);

    void deleteImages(ConstantKeys key);
}
