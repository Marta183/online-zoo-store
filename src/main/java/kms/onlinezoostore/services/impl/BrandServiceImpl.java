package kms.onlinezoostore.services.impl;

import kms.onlinezoostore.dto.BrandDto;
import kms.onlinezoostore.dto.mappers.BrandMapper;
import kms.onlinezoostore.entities.Brand;
import kms.onlinezoostore.exceptions.EntityNotFoundException;
import kms.onlinezoostore.repositories.BrandRepository;
import kms.onlinezoostore.services.BrandService;
import kms.onlinezoostore.utils.UniqueFieldService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final UniqueFieldService uniqueFieldService;
    private static final String ENTITY_CLASS_NAME = "BRAND";

    @Autowired
    public BrandServiceImpl(BrandRepository brandRepository, UniqueFieldService uniqueFieldService) {
        this.brandRepository = brandRepository;
        this.uniqueFieldService = uniqueFieldService;
    }

    @Override
    public BrandDto findById(Long id) {
        log.debug("Finding {} by ID {}", ENTITY_CLASS_NAME, id);

        BrandDto brandDto = brandRepository.findById(id)
                .map(BrandMapper.INSTANCE::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));

        log.debug("Found {} by ID {}", ENTITY_CLASS_NAME, id);
        return brandDto;
    }

    @Override
    public List<BrandDto> findAll() {
        log.debug("Finding all {}", ENTITY_CLASS_NAME);

        return brandRepository.findAll()
                .stream().map(BrandMapper.INSTANCE::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BrandDto create(BrandDto brandDto) {
        log.debug("Creating a new {}: {}", ENTITY_CLASS_NAME, brandDto.getName());

        uniqueFieldService.checkIsFieldValueUniqueOrElseThrow(brandRepository, "name", brandDto.getName());

        Brand brand = BrandMapper.INSTANCE.mapToEntity(brandDto);

        Brand savedbrand = brandRepository.save(brand);
        log.debug("New {} saved in DB with ID {}", ENTITY_CLASS_NAME, savedbrand.getId());

        return BrandMapper.INSTANCE.mapToDto(savedbrand);
    }

    @Override
    @Transactional
    public void update(Long id, BrandDto updatedBrandDto) {
        log.debug("Updating {} with ID {}", ENTITY_CLASS_NAME, id);

        Brand existingBrand = brandRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));

        if (!existingBrand.getName().equals(updatedBrandDto.getName())) {
            uniqueFieldService.checkIsFieldValueUniqueOrElseThrow(brandRepository, "name", updatedBrandDto.getName());
        }

        Brand updatedBrand = BrandMapper.INSTANCE.mapToEntity(updatedBrandDto);
        updatedBrand.setId(id);
        brandRepository.save(updatedBrand);

        log.debug("{} with ID {} updated in DB", ENTITY_CLASS_NAME, id);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.debug("Deleting {} with ID {}", ENTITY_CLASS_NAME, id);

        brandRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));

        brandRepository.deleteById(id);
        log.debug("Deleted {} with ID {}", ENTITY_CLASS_NAME, id);
    }
}
