package kms.onlinezoostore.services.impl;

import kms.onlinezoostore.dto.PrescriptionDto;
import kms.onlinezoostore.dto.mappers.PrescriptionMapper;
import kms.onlinezoostore.entities.Prescription;
import kms.onlinezoostore.exceptions.EntityNotFoundException;
import kms.onlinezoostore.repositories.PrescriptionRepository;
import kms.onlinezoostore.services.PrescriptionService;
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
public class PrescriptionServiceImpl implements PrescriptionService {
    private final PrescriptionMapper prescriptionMapper;
    private final PrescriptionRepository prescriptionRepository;
    private final UniqueFieldService uniqueFieldService;
    private static final String ENTITY_CLASS_NAME = "PRESCRIPTION";

    @Override
    public PrescriptionDto findById(Long id) {
        log.debug("Finding {} by ID {}", ENTITY_CLASS_NAME, id);

        PrescriptionDto prescriptionDto = prescriptionRepository.findById(id).map(prescriptionMapper::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));

        log.debug("Found {} by ID {}", ENTITY_CLASS_NAME, id);
        return prescriptionDto;
    }

    @Override
    public List<PrescriptionDto> findAll() {
        log.debug("Finding all {}", ENTITY_CLASS_NAME);

        return prescriptionRepository.findAll().stream().map(prescriptionMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PrescriptionDto create(PrescriptionDto prescriptionDto) {
        log.debug("Creating a new {}: {}", ENTITY_CLASS_NAME, prescriptionDto.getName());

        uniqueFieldService.checkIsFieldValueUniqueOrElseThrow(prescriptionRepository, "name", prescriptionDto.getName());

        Prescription prescription = prescriptionMapper.mapToEntity(prescriptionDto);

        Prescription savedPrescription = prescriptionRepository.save(prescription);
        log.debug("New {} saved in DB with ID {}", ENTITY_CLASS_NAME, savedPrescription.getId());

        return prescriptionMapper.mapToDto(savedPrescription);
    }

    @Override
    @Transactional
    public void update(Long id, PrescriptionDto updatedPrescriptionDto) {
        log.debug("Updating {} with ID {}", ENTITY_CLASS_NAME, id);

        Prescription existingPrescription = prescriptionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));

        if (!existingPrescription.getName().equals(updatedPrescriptionDto.getName())) {
            uniqueFieldService.checkIsFieldValueUniqueOrElseThrow(prescriptionRepository, "name", updatedPrescriptionDto.getName());
        }
        
        Prescription updatedPrescription = prescriptionMapper.mapToEntity(updatedPrescriptionDto);
        updatedPrescription.setId(id);
        prescriptionRepository.save(updatedPrescription);

        log.debug("{} with ID {} updated in DB", ENTITY_CLASS_NAME, id);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.debug("Deleting {} with ID {}", ENTITY_CLASS_NAME, id);

        prescriptionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));

        prescriptionRepository.deleteById(id);
        log.debug("Deleted {} with ID {}", ENTITY_CLASS_NAME, id);
    }
}
