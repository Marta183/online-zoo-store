package kms.onlinezoostore.services.impl;

import kms.onlinezoostore.dto.MaterialDto;
import kms.onlinezoostore.dto.mappers.MaterialMapper;
import kms.onlinezoostore.entities.Material;
import kms.onlinezoostore.exceptions.EntityNotFoundException;
import kms.onlinezoostore.repositories.MaterialRepository;
import kms.onlinezoostore.services.MaterialService;
import kms.onlinezoostore.utils.UniqueFieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
    public MaterialDto findById(Long id) {
        MaterialDto materialDto = materialRepository.findById(id)
                .map(MaterialMapper.INSTANCE::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));
        // log
        return materialDto;
    }

    @Override
    public List<MaterialDto> findAll() {
        return materialRepository.findAll()
                .stream().map(MaterialMapper.INSTANCE::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MaterialDto create(MaterialDto materialDto) {
        // log
        uniqueFieldService.checkIsFieldValueUniqueOrElseThrow(materialRepository, "name", materialDto.getName());
        // log
        Material material = MaterialMapper.INSTANCE.mapToEntity(materialDto);
        // log
        Material savedMaterial = materialRepository.save(material);
        // log
        MaterialDto savedMaterialDto = MaterialMapper.INSTANCE.mapToDto(savedMaterial);
        // log
        return savedMaterialDto;
    }

    @Override
    @Transactional
    public void update(Long id, MaterialDto updatedMaterialDto) {
        // log
        Material existingMaterial = materialRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));
        // log
        if (!existingMaterial.getName().equals(updatedMaterialDto.getName())) {
            uniqueFieldService.checkIsFieldValueUniqueOrElseThrow(materialRepository, "name", updatedMaterialDto.getName());
        }
        Material updatedMaterial = MaterialMapper.INSTANCE.mapToEntity(updatedMaterialDto);
        // log
        updatedMaterial.setId(id);
        materialRepository.save(updatedMaterial);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Material material = materialRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));
        // log
        materialRepository.deleteById(id);
    }
}
