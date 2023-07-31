package kms.onlinezoostore.services.impl;

import kms.onlinezoostore.entities.Brand;
import kms.onlinezoostore.repositories.BrandRepository;
import kms.onlinezoostore.services.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    @Autowired
    public BrandServiceImpl(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    @Override
    public Brand findById(Long id) {
        return brandRepository.findById(id).orElse(null);
    }

    @Override
    public List<Brand> findAll() {
        return brandRepository.findAll();
    }

    @Override
    @Transactional
    public Brand create(Brand brand) {
        return brandRepository.save(brand);
    }

    @Override
    @Transactional
    public Brand update(Long id, Brand updatedBrand) {
        updatedBrand.setId(id);
        return brandRepository.save(updatedBrand);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        brandRepository.deleteById(id);
    }
}
