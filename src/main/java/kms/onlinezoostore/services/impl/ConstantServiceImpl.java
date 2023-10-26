package kms.onlinezoostore.services.impl;

import kms.onlinezoostore.dto.AttachedFileDto;
import kms.onlinezoostore.dto.ConstantDto;
import kms.onlinezoostore.dto.mappers.ConstantMapper;
import kms.onlinezoostore.entities.Constant;
import kms.onlinezoostore.entities.enums.ConstantKeys;
import kms.onlinezoostore.exceptions.EntityCannotBeUpdated;
import kms.onlinezoostore.exceptions.EntityNotFoundException;
import kms.onlinezoostore.exceptions.files.FileNotFoundException;
import kms.onlinezoostore.exceptions.files.FileUploadException;
import kms.onlinezoostore.repositories.ConstantRepository;
import kms.onlinezoostore.services.ConstantService;
import kms.onlinezoostore.services.files.images.AttachedImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConstantServiceImpl implements ConstantService {
    private final ConstantMapper constantMapper;
    private final ConstantRepository constantRepository;
    private final AttachedImageService attachedImageService;
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
    public void updateValue(ConstantKeys key, Object updatedValue) {
        log.debug("Updating {} value with key {}", ENTITY_CLASS_NAME, key);

        Constant existingConstant = constantRepository.findByKey(key)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, key.toString()));

        if (existingConstant.isAttachedFile()) {
            updateValue(existingConstant, (MultipartFile) updatedValue);
        } else {
            updateValue(existingConstant, updatedValue);
        }

        log.debug("{} with key {} updated in DB", ENTITY_CLASS_NAME, key);
    }

    private void updateValue(Constant constantToUpdate, Object updatedValue) {
        if (Objects.isNull(updatedValue)) {
            constantToUpdate.setValue(null);
        } else if (updatedValue instanceof String) {
            constantToUpdate.setValue((String) updatedValue);
        } else {
            throw new EntityCannotBeUpdated("Unsupportable type of argument. Constant with key "
                    + constantToUpdate.getKey() + " can not be updated");
        }
    }

    private void updateValue(Constant constantToUpdate, MultipartFile image) {

        deleteImageByOwner(constantToUpdate);

        if (Objects.isNull(image)) {
            constantToUpdate.setValue(null);
        } else {
            uploadImageByOwner(constantToUpdate, image);
        }
    }

    private void deleteImageByOwner(Constant constant) {
        ConstantKeys key = constant.getKey();
        log.debug("Deleting all images for {} key {}", ENTITY_CLASS_NAME, key);

        try {
            attachedImageService.deleteAllByOwner(constantMapper.mapToDto(constant));
        } catch (FileNotFoundException ex) {
            throw new FileUploadException("Cannot delete an existing image because of: " + ex.getMessage());
        }
        log.debug("Deleted image for {} key {}", ENTITY_CLASS_NAME, key);
    }

    private AttachedFileDto uploadImageByOwner(Constant constant, MultipartFile image) {
        log.debug("Uploading image for {} key {}", ENTITY_CLASS_NAME, constant.getKey());

        AttachedFileDto uploadedImage = attachedImageService.uploadFileByOwner(constantMapper.mapToDto(constant), image);

        log.debug("Uploaded new image with ID {} for {} key {}", uploadedImage.getId(), ENTITY_CLASS_NAME, constant.getKey());
        return uploadedImage;
    }
}
