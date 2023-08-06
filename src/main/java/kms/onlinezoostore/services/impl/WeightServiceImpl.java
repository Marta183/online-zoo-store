package kms.onlinezoostore.services.impl;

import kms.onlinezoostore.dto.WeightDto;
import kms.onlinezoostore.dto.mappers.WeightMapper;
import kms.onlinezoostore.entities.Weight;
import kms.onlinezoostore.exceptions.EntityNotFoundException;
import kms.onlinezoostore.repositories.WeightRepository;
import kms.onlinezoostore.utils.UniqueFieldService;
import kms.onlinezoostore.services.WeightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class WeightServiceImpl implements WeightService {

    private final WeightRepository weightRepository;
    private final UniqueFieldService uniqueFieldService;
    private static final String ENTITY_CLASS_NAME = "Weight";

    @Autowired
    public WeightServiceImpl(WeightRepository weightRepository, UniqueFieldService uniqueFieldService) {
        this.weightRepository = weightRepository;
        this.uniqueFieldService = uniqueFieldService;
    }

    @Override
    public WeightDto findById(Long id) {
        WeightDto weightDto = weightRepository.findById(id)
                .map(WeightMapper.INSTANCE::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));
        // log
        return weightDto;
    }

    @Override
    public List<WeightDto> findAll() {
        return weightRepository.findAll()
                .stream().map(WeightMapper.INSTANCE::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public WeightDto create(WeightDto weightDto) {
        // log
        uniqueFieldService.checkIsFieldValueUniqueOrElseThrow(weightRepository, "name", weightDto.getName());
        // log
        Weight weight = WeightMapper.INSTANCE.mapToEntity(weightDto);
        // log
        Weight savedWeight = weightRepository.save(weight);
        // log
        WeightDto savedWeightDto = WeightMapper.INSTANCE.mapToDto(savedWeight);
        // log
        return savedWeightDto;
    }

    @Override
    @Transactional
    public void update(Long id, WeightDto updatedWeightDto) {
        // log
        Weight existingWeight = weightRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));
        // log
        if (!existingWeight.getName().equals(updatedWeightDto.getName())) {
            uniqueFieldService.checkIsFieldValueUniqueOrElseThrow(weightRepository, "name", updatedWeightDto.getName());
        }
        // log
        Weight updatedWeight = WeightMapper.INSTANCE.mapToEntity(updatedWeightDto);
        // log
        updatedWeight.setId(id);
        weightRepository.save(updatedWeight);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Weight weight = weightRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));
        // log
        weightRepository.deleteById(id);
    }
}
