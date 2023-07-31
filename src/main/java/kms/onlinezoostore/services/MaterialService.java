package kms.onlinezoostore.services;

import kms.onlinezoostore.entities.Material;

import java.util.List;

public interface MaterialService {

    Material findById(Long id);
    List<Material> findAll();

    Material create(Material material);
    Material update(Long id, Material material);
    void deleteById(Long id);
}
