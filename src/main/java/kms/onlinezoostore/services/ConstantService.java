package kms.onlinezoostore.services;

import kms.onlinezoostore.dto.ConstantDto;
import kms.onlinezoostore.entities.enums.ConstantKeys;

import java.util.List;

public interface ConstantService {

    List<ConstantDto> findAll();

    ConstantDto findByKey(ConstantKeys key);

    ConstantDto updateValue(ConstantKeys key, Object updatedValue);
}
