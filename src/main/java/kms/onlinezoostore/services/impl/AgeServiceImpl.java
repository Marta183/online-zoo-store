package kms.onlinezoostore.services.impl;

import kms.onlinezoostore.dto.AgeDto;
import kms.onlinezoostore.dto.mappers.AgeMapper;
import kms.onlinezoostore.entities.Age;
import kms.onlinezoostore.exceptions.EntityNotFoundException;
import kms.onlinezoostore.repositories.AgeRepository;
import kms.onlinezoostore.services.AgeService;
import kms.onlinezoostore.utils.UniqueFieldService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AgeServiceImpl implements AgeService {
    private final AgeMapper ageMapper;
    private final AgeRepository ageRepository;
    private final UniqueFieldService uniqueFieldService;
    private static final String ENTITY_CLASS_NAME = "AGE";

    @Override
    public AgeDto findById(Long id) {
        log.debug("Finding {} by ID {}", ENTITY_CLASS_NAME, id);
        
        AgeDto ageDto = ageRepository.findById(id).map(ageMapper::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));
        
        log.debug("Found {} by ID {}", ENTITY_CLASS_NAME, id);
        return ageDto;
    }

    @Override
    public List<AgeDto> findAll() {
        log.debug("Finding all {}", ENTITY_CLASS_NAME);

        return ageRepository.findAll().stream().map(ageMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AgeDto create(AgeDto ageDto) {
        log.debug("Creating a new {}: {}", ENTITY_CLASS_NAME, ageDto.getName());

        uniqueFieldService.checkIsFieldValueUniqueOrElseThrow(ageRepository, "name", ageDto.getName());

        Age age = ageMapper.mapToEntity(ageDto);

        Age savedAge = ageRepository.save(age);
        log.debug("New {} saved in DB with ID {}", ENTITY_CLASS_NAME, savedAge.getId());

        return ageMapper.mapToDto(savedAge);
    }

    @Override
    @Transactional
    public void update(Long id, AgeDto updatedAgeDto) {
        log.debug("Updating {} with ID {}", ENTITY_CLASS_NAME, id);
        
        Age existingAge = ageRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));

        if (!existingAge.getName().equals(updatedAgeDto.getName())) {
            uniqueFieldService.checkIsFieldValueUniqueOrElseThrow(ageRepository, "name", updatedAgeDto.getName());
        }

        Age updatedAge = ageMapper.mapToEntity(updatedAgeDto);
        updatedAge.setId(id);
        ageRepository.save(updatedAge);

        log.debug("{} with ID {} updated in DB", ENTITY_CLASS_NAME, id);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.debug("Deleting {} with ID {}", ENTITY_CLASS_NAME, id);
        
        ageRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));

        ageRepository.deleteById(id);
        log.debug("Deleted {} with ID {}", ENTITY_CLASS_NAME, id);
    }
}
