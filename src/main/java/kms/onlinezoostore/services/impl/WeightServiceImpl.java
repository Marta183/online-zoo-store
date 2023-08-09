package kms.onlinezoostore.services.impl;

import kms.onlinezoostore.dto.WeightDto;
import kms.onlinezoostore.dto.mappers.WeightMapper;
import kms.onlinezoostore.entities.Weight;
import kms.onlinezoostore.exceptions.EntityNotFoundException;
import kms.onlinezoostore.repositories.WeightRepository;
import kms.onlinezoostore.utils.UniqueFieldService;
import kms.onlinezoostore.services.WeightService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class WeightServiceImpl implements WeightService {

    private final WeightRepository weightRepository;
    private final UniqueFieldService uniqueFieldService;
    private static final String ENTITY_CLASS_NAME = "WEIGHT";

    @Autowired
    public WeightServiceImpl(WeightRepository weightRepository, UniqueFieldService uniqueFieldService) {
        this.weightRepository = weightRepository;
        this.uniqueFieldService = uniqueFieldService;
    }

    @Override
    public WeightDto findById(Long id) {
        log.debug("Finding {} by ID {}", ENTITY_CLASS_NAME, id);

        WeightDto weightDto = weightRepository.findById(id)
                .map(WeightMapper.INSTANCE::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));

        log.debug("Found {} by ID {}", ENTITY_CLASS_NAME, id);
        return weightDto;
    }

    @Override
    public List<WeightDto> findAll() {
        log.debug("Finding all {}", ENTITY_CLASS_NAME);

        return weightRepository.findAll()
                .stream().map(WeightMapper.INSTANCE::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public WeightDto create(WeightDto weightDto) {
        log.debug("Creating a new {}: {}", ENTITY_CLASS_NAME, weightDto.getName());

        uniqueFieldService.checkIsFieldValueUniqueOrElseThrow(weightRepository, "name", weightDto.getName());

        Weight weight = WeightMapper.INSTANCE.mapToEntity(weightDto);

        Weight savedWeight = weightRepository.save(weight);
        log.debug("New {} saved in DB with ID {}", ENTITY_CLASS_NAME, savedWeight.getId());

        return WeightMapper.INSTANCE.mapToDto(savedWeight);
    }

    @Override
    @Transactional
    public void update(Long id, WeightDto updatedWeightDto) {
        log.debug("Updating {} with ID {}", ENTITY_CLASS_NAME, id);

        Weight existingWeight = weightRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));

        if (!existingWeight.getName().equals(updatedWeightDto.getName())) {
            uniqueFieldService.checkIsFieldValueUniqueOrElseThrow(weightRepository, "name", updatedWeightDto.getName());
        }

        Weight updatedWeight = WeightMapper.INSTANCE.mapToEntity(updatedWeightDto);
        updatedWeight.setId(id);
        weightRepository.save(updatedWeight);

        log.debug("{} with ID {} updated in DB", ENTITY_CLASS_NAME, id);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.debug("Deleting {} with ID {}", ENTITY_CLASS_NAME, id);

        weightRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));

        weightRepository.deleteById(id);
        log.debug("Deleted {} with ID {}", ENTITY_CLASS_NAME, id);
    }
}
