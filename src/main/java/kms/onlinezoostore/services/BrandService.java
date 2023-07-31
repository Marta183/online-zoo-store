package kms.onlinezoostore.services;

import kms.onlinezoostore.entities.Brand;

import java.util.List;

public interface BrandService {

    Brand findById(Long id);
    List<Brand> findAll();

    Brand create(Brand brand);
    Brand update(Long id, Brand brand);
    void deleteById(Long id);
}
