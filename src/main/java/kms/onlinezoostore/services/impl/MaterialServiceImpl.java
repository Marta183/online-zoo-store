package kms.onlinezoostore.services.impl;

import kms.onlinezoostore.dto.MaterialDto;
import kms.onlinezoostore.dto.mappers.MaterialMapper;
import kms.onlinezoostore.entities.Material;
import kms.onlinezoostore.exceptions.EntityNotFoundException;
import kms.onlinezoostore.repositories.MaterialRepository;
import kms.onlinezoostore.services.MaterialService;
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
public class MaterialServiceImpl implements MaterialService {

    private final MaterialRepository materialRepository;
    private final UniqueFieldService uniqueFieldService;
    private static final String ENTITY_CLASS_NAME = "MATERIAL";

    @Autowired
    public MaterialServiceImpl(MaterialRepository materialRepository, UniqueFieldService uniqueFieldService) {
        this.materialRepository = materialRepository;
        this.uniqueFieldService = uniqueFieldService;
    }

    @Override
    public MaterialDto findById(Long id) {
        log.debug("Finding {} by ID {}", ENTITY_CLASS_NAME, id);

        MaterialDto materialDto = materialRepository.findById(id)
                .map(MaterialMapper.INSTANCE::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));

        log.debug("Found {} by ID {}", ENTITY_CLASS_NAME, id);
        return materialDto;
    }

    @Override
    public List<MaterialDto> findAll() {
        log.debug("Finding all {}", ENTITY_CLASS_NAME);

        return materialRepository.findAll()
                .stream().map(MaterialMapper.INSTANCE::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MaterialDto create(MaterialDto materialDto) {
        log.debug("Creating a new {}: {}", ENTITY_CLASS_NAME, materialDto.getName());

        uniqueFieldService.checkIsFieldValueUniqueOrElseThrow(materialRepository, "name", materialDto.getName());

        Material material = MaterialMapper.INSTANCE.mapToEntity(materialDto);

        Material savedMaterial = materialRepository.save(material);
        log.debug("New {} saved in DB with ID {}", ENTITY_CLASS_NAME, savedMaterial.getId());

        return MaterialMapper.INSTANCE.mapToDto(savedMaterial);
    }

    @Override
    @Transactional
    public void update(Long id, MaterialDto updatedMaterialDto) {
        log.debug("Updating {} with ID {}", ENTITY_CLASS_NAME, id);

        Material existingMaterial = materialRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));

        if (!existingMaterial.getName().equals(updatedMaterialDto.getName())) {
            uniqueFieldService.checkIsFieldValueUniqueOrElseThrow(materialRepository, "name", updatedMaterialDto.getName());
        }

        Material updatedMaterial = MaterialMapper.INSTANCE.mapToEntity(updatedMaterialDto);
        updatedMaterial.setId(id);
        materialRepository.save(updatedMaterial);

        log.debug("{} with ID {} updated in DB", ENTITY_CLASS_NAME, id);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.debug("Deleting {} with ID {}", ENTITY_CLASS_NAME, id);

        materialRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));

        materialRepository.deleteById(id);
        log.debug("Deleted {} with ID {}", ENTITY_CLASS_NAME, id);
    }
}
