package kms.onlinezoostore.services.impl;

import kms.onlinezoostore.entities.Material;
import kms.onlinezoostore.exceptions.EntityNotFoundException;
import kms.onlinezoostore.repositories.MaterialRepository;
import kms.onlinezoostore.services.MaterialService;
import kms.onlinezoostore.utils.UniqueFieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MaterialServiceImpl implements MaterialService {

    private final MaterialRepository materialRepository;
    private final UniqueFieldService uniqueFieldService;
    private static final String ENTITY_CLASS_NAME = "Material";

    @Autowired
    public MaterialServiceImpl(MaterialRepository materialRepository, UniqueFieldService uniqueFieldService) {
        this.materialRepository = materialRepository;
        this.uniqueFieldService = uniqueFieldService;
    }

    @Override
    public Material findById(Long id) {
        Material material = materialRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));
        // log
        return material;
    }

    @Override
    public List<Material> findAll() {
        return materialRepository.findAll();
    }

    @Override
    @Transactional
    public Material create(Material material) {
        // log
        uniqueFieldService.checkIsFieldValueUniqueOrElseThrow(materialRepository, "name", material.getName());
        // log
        Material savedMaterial = materialRepository.save(material);
        // log
        return savedMaterial;
    }

    @Override
    @Transactional
    public Material update(Long id, Material updatedMaterial) {
        // log
        Material existingMaterial = materialRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));
        // log
        if (!existingMaterial.getName().equals(updatedMaterial.getName())) {
            uniqueFieldService.checkIsFieldValueUniqueOrElseThrow(materialRepository, "name", updatedMaterial.getName());
        }
        // log
        updatedMaterial.setId(id);
        return materialRepository.save(updatedMaterial);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        // log
        materialRepository.deleteById(id);
    }
}
