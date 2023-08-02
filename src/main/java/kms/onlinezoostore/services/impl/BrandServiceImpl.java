package kms.onlinezoostore.services.impl;

import kms.onlinezoostore.entities.Brand;
import kms.onlinezoostore.exceptions.EntityNotFoundException;
import kms.onlinezoostore.repositories.BrandRepository;
import kms.onlinezoostore.services.BrandService;
import kms.onlinezoostore.utils.UniqueFieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public Brand findById(Long id) {
        Brand brand = brandRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));
        // log
        return brand;
    }

    @Override
    public List<Brand> findAll() {
        return brandRepository.findAll();
    }

    @Override
    @Transactional
    public Brand create(Brand brand) {
        // log
        uniqueFieldService.checkIsFieldValueUniqueOrElseThrow(brandRepository, "name", brand.getName());
        // log
        Brand savedbrand = brandRepository.save(brand);
        // log
        return savedbrand;
    }

    @Override
    @Transactional
    public Brand update(Long id, Brand updatedBrand) {
        // log
        Brand existingBrand = brandRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));
        // log
        if (!existingBrand.getName().equals(updatedBrand.getName())) {
            uniqueFieldService.checkIsFieldValueUniqueOrElseThrow(brandRepository, "name", updatedBrand.getName());
        }
        // log
        updatedBrand.setId(id);
        return brandRepository.save(updatedBrand);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        // log
        brandRepository.deleteById(id);
    }
}
