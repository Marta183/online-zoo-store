package kms.onlinezoostore.services.impl;

import kms.onlinezoostore.entities.Material;
import kms.onlinezoostore.repositories.MaterialRepository;
import kms.onlinezoostore.services.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MaterialServiceImpl implements MaterialService {

    private final MaterialRepository materialRepository;
    @Autowired
    public MaterialServiceImpl(MaterialRepository materialRepository) {
        this.materialRepository = materialRepository;
    }

    @Override
    public Material findById(Long id) {
        return materialRepository.findById(id).orElse(null);
    }

    @Override
    public List<Material> findAll() {
        return materialRepository.findAll();
    }

    @Override
    @Transactional
    public Material create(Material material) {
        return materialRepository.save(material);
    }

    @Override
    @Transactional
    public Material update(Long id, Material updatedMaterial) {
        updatedMaterial.setId(id);
        return materialRepository.save(updatedMaterial);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        materialRepository.deleteById(id);
    }
}
