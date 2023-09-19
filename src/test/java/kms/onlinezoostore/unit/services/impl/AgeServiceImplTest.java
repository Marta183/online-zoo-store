package kms.onlinezoostore.unit.services.impl;

import kms.onlinezoostore.dto.AgeDto;
import kms.onlinezoostore.dto.mappers.AgeMapper;
import kms.onlinezoostore.entities.Age;
import kms.onlinezoostore.exceptions.EntityDuplicateException;
import kms.onlinezoostore.exceptions.EntityNotFoundException;
import kms.onlinezoostore.repositories.AgeRepository;
import kms.onlinezoostore.services.impl.AgeServiceImpl;
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
class AgeServiceImplTest {

    @Mock
    private AgeRepository ageRepository;
    @Spy
    private UniqueFieldService uniqueFieldService = new UniqueFieldServiceImpl();
    @Spy
    private AgeMapper ageMapper = Mappers.getMapper(AgeMapper.class);
    @InjectMocks
    private AgeServiceImpl ageService;
    
    private Age age;
    private AgeDto ageDto;

    @BeforeEach
    public void setup() {
        age = new Age(1L, "test1");
        ageDto = ageMapper.mapToDto(age);
    }

    @Test
    void findById_ShouldReturnAgeDto_WhenIdExists() {
        when(ageRepository.findById(age.getId())).thenReturn(Optional.of(age));

        AgeDto ageDtoActual = ageService.findById(age.getId());

        assertEquals(ageDto, ageDtoActual, "AgeDto: expected and actual is not equal.");
    }

    @Test
    void findById_ShouldThrowException_WhenIdNotFound() {
        when(ageRepository.findById(age.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> ageService.findById(age.getId()));
    }

    @Test
    void findAll_ShouldReturnAgeList() {
        List<Age> ageList = new ArrayList<>(){{ add(age); }};
        ageList.add(new Age(2L, "test2"));
        ageList.add(new Age(3L, "test3"));

        List<AgeDto> ageDtoList = ageList.stream().map(ageMapper::mapToDto).collect(Collectors.toList());

        when(ageRepository.findAll()).thenReturn(ageList);

        // act
        List<AgeDto> actualAgeDtoList = ageService.findAll();

        assertEquals(ageDtoList.size(), actualAgeDtoList.size(), "AgeDto list size: expected and actual is not equal.");
        assertIterableEquals(ageDtoList, actualAgeDtoList, "AgeDto list: expected and actual don't have the same elements in the same order.");
    }

    @Test
    void findAll_ShouldReturnEmptyList() {
        when(ageRepository.findAll()).thenReturn(Collections.emptyList());

        List<AgeDto> actualAgeDtoList = ageService.findAll();

        assertEquals(0, actualAgeDtoList.size(), "Actual ageDto list is not empty");
    }

    @Test
    void create_ShouldReturnAgeDto() {
        when(ageRepository.count(any(Specification.class))).thenReturn(0L);
        when(ageRepository.save(any(Age.class))).thenReturn(age);

        AgeDto actualSavedAgeDto = ageService.create(ageDto);

        assertNotNull(actualSavedAgeDto);
        assertNotNull(actualSavedAgeDto.getId());
        assertEquals(ageDto.getName(), actualSavedAgeDto.getName(), "AgeDto name: expected and actual is not equal.");
    }

    @Test
    void create_ShouldThrowException_WhenNameIsNotUnique() {
        when(ageRepository.count(any(Specification.class))).thenReturn(1L);

        assertThrows(EntityDuplicateException.class, () -> ageService.create(ageDto));

        verify(ageRepository, never()).save(any(Age.class));
    }

    @Test
    void update_DataIsCorrect() {
        when(ageRepository.findById(ageDto.getId())).thenReturn(Optional.of(age));

        ageService.update(ageDto.getId(), ageDto);

        verify(ageRepository, times(1)).save(any(Age.class));
    }

    @Test
    void update_ShouldThrowException_WhenIdNotFound() {
        when(ageRepository.findById(ageDto.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> ageService.update(ageDto.getId(), ageDto));

        verify(ageRepository, never()).save(any(Age.class));
    }

    @Test
    void update_ShouldThrowException_WhenNameIsNotUnique() {
        Age ageExisting = new Age(1L, "test1");
        AgeDto ageDtoToUpdate = new AgeDto(1L, "test2");

        when(ageRepository.count(any(Specification.class))).thenReturn(1L);
        when(ageRepository.findById(anyLong())).thenReturn(Optional.of(ageExisting));

        assertThrows(EntityDuplicateException.class, () -> ageService.update(ageDtoToUpdate.getId(), ageDtoToUpdate));

        verify(ageRepository, never()).save(any(Age.class));
    }

    @Test
    void deleteById_WhenDataIsCorrect() {
        when(ageRepository.findById(1L)).thenReturn(Optional.of(age));

        ageService.deleteById(1L);

        verify(ageRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteById_ShouldThrowException_WhenIdNotFound() {
        when(ageRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> ageService.deleteById(1L));

        verify(ageRepository, never()).deleteById(anyLong());
    }
}
