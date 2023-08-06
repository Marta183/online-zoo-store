package kms.onlinezoostore.services.impl;

import kms.onlinezoostore.dto.AgeDto;
import kms.onlinezoostore.dto.mappers.AgeMapper;
import kms.onlinezoostore.entities.Age;
import kms.onlinezoostore.exceptions.EntityNotFoundException;
import kms.onlinezoostore.repositories.AgeRepository;
import kms.onlinezoostore.services.AgeService;
import kms.onlinezoostore.utils.UniqueFieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AgeServiceImpl implements AgeService {

    private final AgeRepository ageRepository;
    private final UniqueFieldService uniqueFieldService;
    private static final String ENTITY_CLASS_NAME = "Age";

    @Autowired
    public AgeServiceImpl(AgeRepository ageRepository, UniqueFieldService uniqueFieldService) {
        this.ageRepository = ageRepository;
        this.uniqueFieldService = uniqueFieldService;
    }

    @Override
    public AgeDto findById(Long id) {
        AgeDto ageDto = ageRepository.findById(id)
                .map(AgeMapper.INSTANCE::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));
        // log
        return ageDto;
    }

    @Override
    public List<AgeDto> findAll() {
        return ageRepository.findAll()
                .stream().map(AgeMapper.INSTANCE::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AgeDto create(AgeDto ageDto) {
        // log
        uniqueFieldService.checkIsFieldValueUniqueOrElseThrow(ageRepository, "name", ageDto.getName());
        // log
        Age age = AgeMapper.INSTANCE.mapToEntity(ageDto);
        // log
        Age savedAge = ageRepository.save(age);
        // log
        AgeDto savedAgeDto = AgeMapper.INSTANCE.mapToDto(savedAge);
        // log
        return savedAgeDto;
    }

    @Override
    @Transactional
    public void update(Long id, AgeDto updatedAgeDto) {
        // log
        Age existingAge = ageRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));
        // log
        if (!existingAge.getName().equals(updatedAgeDto.getName())) {
            uniqueFieldService.checkIsFieldValueUniqueOrElseThrow(ageRepository, "name", updatedAgeDto.getName());
        }

        Age updatedAge = AgeMapper.INSTANCE.mapToEntity(updatedAgeDto);
        // log
        updatedAge.setId(id);
        ageRepository.save(updatedAge);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Age age = ageRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));
        // log
        ageRepository.deleteById(id);
    }
}
