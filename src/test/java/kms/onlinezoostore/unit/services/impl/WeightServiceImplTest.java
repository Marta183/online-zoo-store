package kms.onlinezoostore.unit.services.impl;

import kms.onlinezoostore.dto.WeightDto;
import kms.onlinezoostore.dto.mappers.WeightMapper;
import kms.onlinezoostore.entities.Weight;
import kms.onlinezoostore.exceptions.EntityDuplicateException;
import kms.onlinezoostore.exceptions.EntityNotFoundException;
import kms.onlinezoostore.repositories.WeightRepository;
import kms.onlinezoostore.services.impl.WeightServiceImpl;
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
class WeightServiceImplTest {

    @Mock
    private WeightRepository weightRepository;
    @Spy
    private UniqueFieldService uniqueFieldService = new UniqueFieldServiceImpl();
    @Spy
    private WeightMapper weightMapper = Mappers.getMapper(WeightMapper.class);
    @InjectMocks
    private WeightServiceImpl weightService;

    private Weight weight;
    private WeightDto weightDto;

    @BeforeEach
    public void setup(){
        weight = new Weight(1L, "test1");
        weightDto = weightMapper.mapToDto(weight);
    }

    @Test
    void findById_ShouldReturnWeightDto_WhenIdExists() {
        when(weightRepository.findById(weight.getId())).thenReturn(Optional.of(weight));

        WeightDto weightDtoActual = weightService.findById(weight.getId());

        assertEquals(weightDto, weightDtoActual, "WeightDto: expected and actual is not equal.");
    }

    @Test
    void findById_ShouldThrowException_WhenIdNotFound() {
        when(weightRepository.findById(weight.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> weightService.findById(weight.getId()));
    }

    @Test
    void findAll_ShouldReturnWeightList() {
        List<Weight> weightList = new ArrayList<>(){{ add(weight); }};
        weightList.add(new Weight(2L, "test2"));
        weightList.add(new Weight(3L, "test3"));

        List<WeightDto> weightDtoList = weightList.stream().map((el) -> weightMapper.mapToDto(el)).collect(Collectors.toList());

        when(weightRepository.findAll()).thenReturn(weightList);

        // act
        List<WeightDto> actualWeightDtoList = weightService.findAll();

        assertEquals(weightDtoList.size(), actualWeightDtoList.size(), "WeightDto list size: expected and actual is not equal.");
        assertIterableEquals(weightDtoList, actualWeightDtoList, "WeightDto list: expected and actual don't have the same elements in the same order.");
    }

    @Test
    void findAll_ShouldReturnEmptyList() {
        when(weightRepository.findAll()).thenReturn(Collections.emptyList());

        List<WeightDto> actualWeightDtoList = weightService.findAll();

        assertEquals(0, actualWeightDtoList.size(), "Actual weightDto list is not empty");
    }

    @Test
    void create_ShouldReturnWeightDto() {
        when(weightRepository.count(any(Specification.class))).thenReturn(0L);
        when(weightRepository.save(any(Weight.class))).thenReturn(weight);

        WeightDto actualSavedWeightDto = weightService.create(weightDto);

        assertNotNull(actualSavedWeightDto);
        assertNotNull(actualSavedWeightDto.getId());
        assertEquals(weightDto.getName(), actualSavedWeightDto.getName(), "WeightDto name: expected and actual is not equal.");
    }

    @Test
    void create_ShouldThrowException_WhenNameIsNotUnique() {
        when(weightRepository.count(any(Specification.class))).thenReturn(1L);

        assertThrows(EntityDuplicateException.class, () -> weightService.create(weightDto));

        verify(weightRepository, never()).save(any(Weight.class));
    }

    @Test
    void update_DataIsCorrect() {
        when(weightRepository.findById(weightDto.getId())).thenReturn(Optional.of(weight));

        weightService.update(weightDto.getId(), weightDto);

        verify(weightRepository, times(1)).save(any(Weight.class));
    }

    @Test
    void update_ShouldThrowException_WhenIdNotFound() {
        when(weightRepository.findById(weightDto.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> weightService.update(weightDto.getId(), weightDto));

        verify(weightRepository, never()).save(any(Weight.class));
    }

    @Test
    void update_ShouldThrowException_WhenNameIsNotUnique() {
        Weight weightExisting = new Weight(1L, "test1");
        WeightDto weightDtoToUpdate = new WeightDto(1L, "test2");

        when(weightRepository.count(any(Specification.class))).thenReturn(1L);
        when(weightRepository.findById(anyLong())).thenReturn(Optional.of(weightExisting));

        assertThrows(EntityDuplicateException.class, () -> weightService.update(weightDtoToUpdate.getId(), weightDtoToUpdate));

        verify(weightRepository, never()).save(any(Weight.class));
    }

    @Test
    void deleteById_WhenDataIsCorrect() {
        when(weightRepository.findById(1L)).thenReturn(Optional.of(weight));

        weightService.deleteById(1L);

        verify(weightRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteById_ShouldThrowException_WhenIdNotFound() {
        when(weightRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> weightService.deleteById(1L));

        verify(weightRepository, never()).deleteById(anyLong());
    }
}
