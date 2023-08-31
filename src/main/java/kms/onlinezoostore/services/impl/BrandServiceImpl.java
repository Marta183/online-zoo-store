package kms.onlinezoostore.services.impl;

import kms.onlinezoostore.dto.AttachedFileDto;
import kms.onlinezoostore.dto.BrandDto;
import kms.onlinezoostore.dto.mappers.BrandMapper;
import kms.onlinezoostore.entities.Brand;
import kms.onlinezoostore.exceptions.EntityNotFoundException;
import kms.onlinezoostore.exceptions.files.FileNotFoundException;
import kms.onlinezoostore.exceptions.files.FileUploadException;
import kms.onlinezoostore.repositories.BrandRepository;
import kms.onlinezoostore.services.BrandService;
import kms.onlinezoostore.services.files.images.AttachedImageService;
import kms.onlinezoostore.utils.UniqueFieldService;
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
public class BrandServiceImpl implements BrandService {
    private final BrandMapper brandMapper;
    private final BrandRepository brandRepository;
    private final UniqueFieldService uniqueFieldService;
    private final AttachedImageService attachedImageService;
    private static final String ENTITY_CLASS_NAME = "BRAND";

    @Override
    public BrandDto findById(Long id) {
        log.debug("Finding {} by ID {}", ENTITY_CLASS_NAME, id);

        BrandDto brandDto = brandRepository.findById(id).map(brandMapper::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));

        log.debug("Found {} by ID {}", ENTITY_CLASS_NAME, id);
        return brandDto;
    }

    @Override
    public List<BrandDto> findAll() {
        log.debug("Finding all {}", ENTITY_CLASS_NAME);

        return brandRepository.findAll().stream().map(brandMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BrandDto create(BrandDto brandDto) {
        log.debug("Creating a new {}: {}", ENTITY_CLASS_NAME, brandDto.getName());

        uniqueFieldService.checkIsFieldValueUniqueOrElseThrow(brandRepository, "name", brandDto.getName());

        Brand brand = brandMapper.mapToEntity(brandDto);

        Brand savedbrand = brandRepository.save(brand);
        log.debug("New {} saved in DB with ID {}", ENTITY_CLASS_NAME, savedbrand.getId());

        return brandMapper.mapToDto(savedbrand);
    }

    @Override
    @Transactional
    public void update(Long id, BrandDto updatedBrandDto) {
        log.debug("Updating {} with ID {}", ENTITY_CLASS_NAME, id);

        Brand existingBrand = brandRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));

        if (!existingBrand.getName().equals(updatedBrandDto.getName())) {
            uniqueFieldService.checkIsFieldValueUniqueOrElseThrow(brandRepository, "name", updatedBrandDto.getName());
        }

        Brand updatedBrand = brandMapper.mapToEntity(updatedBrandDto);
        updatedBrand.setId(id);
        brandRepository.save(updatedBrand);

        log.debug("{} with ID {} updated in DB", ENTITY_CLASS_NAME, id);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.debug("Deleting {} with ID {}", ENTITY_CLASS_NAME, id);

        BrandDto brandDto = brandRepository.findById(id).map(brandMapper::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));

        // delete entity
        brandRepository.deleteById(id);
        log.debug("Deleted {} with ID {}", ENTITY_CLASS_NAME, id);

        // delete attached files
        attachedImageService.deleteAllByOwner(brandDto);
        log.debug("Deleted image for {} with ID {}", ENTITY_CLASS_NAME, id);
    }


    //// IMAGES ////

    @Override
    public AttachedFileDto findImageByOwnerId(Long id) {
        log.debug("Finding image by {} ID {}", ENTITY_CLASS_NAME, id);

        BrandDto brandDto = brandRepository.findById(id)
                .map(brandMapper::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));

        AttachedFileDto attachedFileDto = attachedImageService.findFirstByOwner(brandDto);
        log.debug("Found image with ID {} by {} ID {}", attachedFileDto.getId(), ENTITY_CLASS_NAME, id);

        return attachedFileDto;
    }

    @Override
    @Transactional
    public AttachedFileDto uploadImageByOwnerId(Long id, MultipartFile image) {
        log.debug("Uploading image for {} ID {}", ENTITY_CLASS_NAME, id);

        BrandDto brandDto = brandRepository.findById(id)
                .map(brandMapper::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));

        // delete existing image
        log.debug("Replacing existing image for {} ID {}", ENTITY_CLASS_NAME, id);
        try {
            attachedImageService.deleteAllByOwner(brandDto);
        } catch (FileNotFoundException ex) {
            throw new FileUploadException("Cannot replace an existing image because of: " + ex.getMessage());
        }

        // upload new image
        AttachedFileDto uploadedImage = attachedImageService.uploadFileByOwner(brandDto, image);
        log.debug("Uploaded new image with ID {} for {} ID {}", uploadedImage.getId(), ENTITY_CLASS_NAME, id);

        return uploadedImage;
    }

    @Override
    @Transactional
    public void deleteImageByOwnerId(Long id) {
        log.debug("Deleting all images for {} ID {}", ENTITY_CLASS_NAME, id);

        BrandDto brandDto = brandRepository.findById(id)
                .map(brandMapper::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));

        attachedImageService.deleteAllByOwner(brandDto);
        log.debug("Deleted all images for {} ID {}", ENTITY_CLASS_NAME, id);
    }
}
