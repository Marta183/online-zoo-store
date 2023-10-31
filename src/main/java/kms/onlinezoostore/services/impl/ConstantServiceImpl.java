package kms.onlinezoostore.services.impl;

import kms.onlinezoostore.dto.AttachedFileDto;
import kms.onlinezoostore.dto.ConstantDto;
import kms.onlinezoostore.dto.mappers.AttachedFileMapper;
import kms.onlinezoostore.dto.mappers.ConstantMapper;
import kms.onlinezoostore.entities.Constant;
import kms.onlinezoostore.entities.enums.ConstantKeys;
import kms.onlinezoostore.exceptions.EntityCannotBeUpdated;
import kms.onlinezoostore.exceptions.EntityNotFoundException;
import kms.onlinezoostore.repositories.ConstantRepository;
import kms.onlinezoostore.services.ConstantService;
import kms.onlinezoostore.services.files.images.AttachedImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConstantServiceImpl implements ConstantService {
    private final ConstantMapper constantMapper;
    private final ConstantRepository constantRepository;
    private final AttachedImageService attachedImageService;
    private final AttachedFileMapper attachedFileMapper;
    private static final String ENTITY_CLASS_NAME = "CONSTANT";

    @Override
    public List<ConstantDto> findAll() {
        log.debug("Finding all {}", ENTITY_CLASS_NAME);

        return constantRepository.findAll().stream().map(constantMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ConstantDto findByKey(ConstantKeys key) {
        log.debug("Finding {} value by key {}", ENTITY_CLASS_NAME, key);

        ConstantDto constantDto = constantRepository.findByKey(key)
                .map(constantMapper::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, key.toString()));

        log.debug("Found {} value by key {}", ENTITY_CLASS_NAME, key);
        return constantDto;
    }

    @Override
    @Transactional
    public Object updateValue(ConstantKeys key, Object updatedValue) {
        log.debug("Updating {} value with key {}", ENTITY_CLASS_NAME, key);

        Constant existingConstant = constantRepository.findByKey(key)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, key.toString()));

        verifyValueType(existingConstant, updatedValue);

        return existingConstant.isAttachedFile() ?
                updateValue(existingConstant, (MultipartFile) updatedValue) :
                updateValue(existingConstant, (String) updatedValue);
    }

    @Override
    @Transactional
    public void deleteImages(ConstantKeys key) {
        log.debug("Deleting {} value with key {}", ENTITY_CLASS_NAME, key);

        Constant existingConstant = constantRepository.findByKey(key)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, key.toString()));

        attachedImageService.deleteAllByOwner(constantMapper.mapToDto(existingConstant));
        log.debug("Deleted all images for {} with key {}", ENTITY_CLASS_NAME, key);
    }

    private void verifyValueType(Constant constant, Object updatedValue) {
        if (constant.isAttachedFile() && updatedValue instanceof MultipartFile)
            return;
        if (!constant.isAttachedFile() && updatedValue instanceof String)
            return;

        throw new EntityCannotBeUpdated("Unsupportable type of argument. Constant with key "
                + constant.getKey() + " can not be updated");
    }

    private ConstantDto updateValue(Constant constantToUpdate, String updatedValue) {
        constantToUpdate.setValue(updatedValue);
        log.debug("Updated string value for {} with key {} ", ENTITY_CLASS_NAME, constantToUpdate.getKey());

        return constantMapper.mapToDto(constantToUpdate);
    }

    private AttachedFileDto updateValue(Constant constant, MultipartFile image) {
        log.debug("Updating image for {} key {}", ENTITY_CLASS_NAME, constant.getKey());

        AttachedFileDto uploadedImage = attachedImageService.replaceFileByOwner(constantMapper.mapToDto(constant), image);

        log.debug("Updated image ID = {} for {} with key {}", uploadedImage.getId(), ENTITY_CLASS_NAME, constant.getKey());
        return uploadedImage;
    }
}
