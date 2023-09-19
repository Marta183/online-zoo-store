package kms.onlinezoostore.unit.services.impl;

import kms.onlinezoostore.dto.PrescriptionDto;
import kms.onlinezoostore.dto.mappers.PrescriptionMapper;
import kms.onlinezoostore.entities.Prescription;
import kms.onlinezoostore.exceptions.EntityDuplicateException;
import kms.onlinezoostore.exceptions.EntityNotFoundException;
import kms.onlinezoostore.repositories.PrescriptionRepository;
import kms.onlinezoostore.services.impl.PrescriptionServiceImpl;
import kms.onlinezoostore.utils.UniqueFieldService;
import kms.onlinezoostore.utils.UniqueFieldServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;

@ExtendWith(MockitoExtension.class)
class PrescriptionServiceImplTest {

    @Mock
    private PrescriptionRepository prescriptionRepository;
    @Spy
    private UniqueFieldService uniqueFieldService = new UniqueFieldServiceImpl();
    @Spy
    private PrescriptionMapper prescriptionMapper = Mappers.getMapper(PrescriptionMapper.class);
    @InjectMocks
    private PrescriptionServiceImpl prescriptionService;

    private Prescription prescription;
    private PrescriptionDto prescriptionDto;

    @BeforeEach
    public void setup() {
        prescription = new Prescription(1L, "test1");
        prescriptionDto = prescriptionMapper.mapToDto(prescription);
    }

    @Test
    void findById_ShouldReturnPrescriptionDto_WhenIdExists() {
        when(prescriptionRepository.findById(prescription.getId())).thenReturn(Optional.of(prescription));

        PrescriptionDto prescriptionDtoActual = prescriptionService.findById(prescription.getId());

        assertEquals(prescriptionDto, prescriptionDtoActual, "PrescriptionDto: expected and actual is not equal.");
    }

    @Test
    void findById_ShouldThrowException_WhenIdNotFound() {
        when(prescriptionRepository.findById(prescription.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> prescriptionService.findById(prescription.getId()));
    }

    @Test
    void findAll_ShouldReturnPrescriptionList() {
        List<Prescription> prescriptionList = new ArrayList<>(){{ add(prescription); }};
        prescriptionList.add(new Prescription(2L, "test2"));
        prescriptionList.add(new Prescription(3L, "test3"));

        List<PrescriptionDto> prescriptionDtoList = prescriptionList.stream().map(prescriptionMapper::mapToDto).collect(Collectors.toList());

        when(prescriptionRepository.findAll()).thenReturn(prescriptionList);

        // act
        List<PrescriptionDto> actualPrescriptionDtoList = prescriptionService.findAll();

        assertEquals(prescriptionDtoList.size(), actualPrescriptionDtoList.size(), "PrescriptionDto list size: expected and actual is not equal.");
        assertIterableEquals(prescriptionDtoList, actualPrescriptionDtoList, "PrescriptionDto list: expected and actual don't have the same elements in the same order.");
    }

    @Test
    void findAll_ShouldReturnEmptyList() {
        when(prescriptionRepository.findAll()).thenReturn(Collections.emptyList());

        List<PrescriptionDto> actualPrescriptionDtoList = prescriptionService.findAll();

        assertEquals(0, actualPrescriptionDtoList.size(), "Actual prescriptionDto list is not empty");
    }

    @Test
    void create_ShouldReturnPrescriptionDto() {
        when(prescriptionRepository.count(any(Specification.class))).thenReturn(0L);
        when(prescriptionRepository.save(any(Prescription.class))).thenReturn(prescription);

        PrescriptionDto actualSavedPrescriptionDto = prescriptionService.create(prescriptionDto);

        assertNotNull(actualSavedPrescriptionDto);
        assertNotNull(actualSavedPrescriptionDto.getId());
        assertEquals(prescriptionDto.getName(), actualSavedPrescriptionDto.getName(), "PrescriptionDto name: expected and actual is not equal.");
    }

    @Test
    void create_ShouldThrowException_WhenNameIsNotUnique() {
        when(prescriptionRepository.count(any(Specification.class))).thenReturn(1L);

        assertThrows(EntityDuplicateException.class, () -> prescriptionService.create(prescriptionDto));

        verify(prescriptionRepository, never()).save(any(Prescription.class));
    }

    @Test
    void update_DataIsCorrect() {
        when(prescriptionRepository.findById(prescriptionDto.getId())).thenReturn(Optional.of(prescription));

        prescriptionService.update(prescriptionDto.getId(), prescriptionDto);

        verify(prescriptionRepository, times(1)).save(any(Prescription.class));
    }

    @Test
    void update_ShouldThrowException_WhenIdNotFound() {
        when(prescriptionRepository.findById(prescriptionDto.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> prescriptionService.update(prescriptionDto.getId(), prescriptionDto));

        verify(prescriptionRepository, never()).save(any(Prescription.class));
    }

    @Test
    void update_ShouldThrowException_WhenNameIsNotUnique() {
        Prescription prescriptionExisting = new Prescription(1L, "test1");
        PrescriptionDto prescriptionDtoToUpdate = new PrescriptionDto(1L, "test2");

        when(prescriptionRepository.count(any(Specification.class))).thenReturn(1L);
        when(prescriptionRepository.findById(anyLong())).thenReturn(Optional.of(prescriptionExisting));

        assertThrows(EntityDuplicateException.class, () -> prescriptionService.update(prescriptionDtoToUpdate.getId(), prescriptionDtoToUpdate));

        verify(prescriptionRepository, never()).save(any(Prescription.class));
    }

    @Test
    void deleteById_WhenDataIsCorrect() {
        when(prescriptionRepository.findById(1L)).thenReturn(Optional.of(prescription));

        prescriptionService.deleteById(1L);

        verify(prescriptionRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteById_ShouldThrowException_WhenIdNotFound() {
        when(prescriptionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> prescriptionService.deleteById(1L));

        verify(prescriptionRepository, never()).deleteById(anyLong());
    }
}
