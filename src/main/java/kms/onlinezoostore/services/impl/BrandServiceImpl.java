package kms.onlinezoostore.services.impl;

import kms.onlinezoostore.dto.BrandDto;
import kms.onlinezoostore.dto.mappers.BrandMapper;
import kms.onlinezoostore.entities.Brand;
import kms.onlinezoostore.exceptions.EntityNotFoundException;
import kms.onlinezoostore.repositories.BrandRepository;
import kms.onlinezoostore.services.BrandService;
import kms.onlinezoostore.utils.UniqueFieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final UniqueFieldService uniqueFieldService;
    private static final String ENTITY_CLASS_NAME = "Brand";

    @Autowired
    public BrandServiceImpl(BrandRepository brandRepository, UniqueFieldService uniqueFieldService) {
        this.brandRepository = brandRepository;
        this.uniqueFieldService = uniqueFieldService;
    }

    @Override
    public BrandDto findById(Long id) {
        BrandDto brandDto = brandRepository.findById(id)
                .map(BrandMapper.INSTANCE::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));
        // log
        return brandDto;
    }

    @Override
    public List<BrandDto> findAll() {
        return brandRepository.findAll()
                .stream().map(BrandMapper.INSTANCE::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BrandDto create(BrandDto brandDto) {
        // log
        uniqueFieldService.checkIsFieldValueUniqueOrElseThrow(brandRepository, "name", brandDto.getName());
        // log
        Brand brand = BrandMapper.INSTANCE.mapToEntity(brandDto);
        // log
        Brand savedbrand = brandRepository.save(brand);
        // log
        BrandDto savedbrandDto = BrandMapper.INSTANCE.mapToDto(savedbrand);
        // log
        return savedbrandDto;
    }

    @Override
    @Transactional
    public void update(Long id, BrandDto updatedBrandDto) {
        // log
        Brand existingBrand = brandRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));
        // log
        if (!existingBrand.getName().equals(updatedBrandDto.getName())) {
            uniqueFieldService.checkIsFieldValueUniqueOrElseThrow(brandRepository, "name", updatedBrandDto.getName());
        }
        // log
        Brand updatedBrand = BrandMapper.INSTANCE.mapToEntity(updatedBrandDto);
        // log
        updatedBrand.setId(id);
        brandRepository.save(updatedBrand);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Brand brand = brandRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));
        // log
        brandRepository.deleteById(id);
    }
}
