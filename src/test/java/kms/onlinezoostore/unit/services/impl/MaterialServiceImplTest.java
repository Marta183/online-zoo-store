package kms.onlinezoostore.unit.services.impl;

import kms.onlinezoostore.dto.MaterialDto;
import kms.onlinezoostore.dto.mappers.MaterialMapper;
import kms.onlinezoostore.entities.Material;
import kms.onlinezoostore.exceptions.EntityDuplicateException;
import kms.onlinezoostore.exceptions.EntityNotFoundException;
import kms.onlinezoostore.repositories.MaterialRepository;
import kms.onlinezoostore.services.impl.MaterialServiceImpl;
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
class MaterialServiceImplTest {

    @Mock
    private MaterialRepository materialRepository;
    @Spy
    private UniqueFieldService uniqueFieldService = new UniqueFieldServiceImpl();
    @Spy
    private MaterialMapper materialMapper = Mappers.getMapper(MaterialMapper.class);
    @InjectMocks
    private MaterialServiceImpl materialService;
    
    private Material material;
    private MaterialDto materialDto;

    @BeforeEach
    public void setup(){
        material = new Material(1L, "test1");
        materialDto = materialMapper.mapToDto(material);
    }

    @Test
    void findById_ShouldReturnMaterialDto_WhenIdExists() {
        when(materialRepository.findById(material.getId())).thenReturn(Optional.of(material));

        MaterialDto materialDtoActual = materialService.findById(material.getId());

        assertEquals(materialDto, materialDtoActual, "MaterialDto: expected and actual is not equal.");
    }

    @Test
    void findById_ShouldThrowException_WhenIdNotFound() {
        when(materialRepository.findById(material.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> materialService.findById(material.getId()));
    }

    @Test
    void findAll_ShouldReturnMaterialList() {
        List<Material> materialList = new ArrayList<>(){{ add(material); }};
        materialList.add(new Material(2L, "test2"));
        materialList.add(new Material(3L, "test3"));

        List<MaterialDto> materialDtoList = materialList.stream().map((el) -> materialMapper.mapToDto(el)).collect(Collectors.toList());

        when(materialRepository.findAll()).thenReturn(materialList);

        // act
        List<MaterialDto> actualMaterialDtoList = materialService.findAll();

        assertEquals(materialDtoList.size(), actualMaterialDtoList.size(), "MaterialDto list size: expected and actual is not equal.");
        assertIterableEquals(materialDtoList, actualMaterialDtoList, "MaterialDto list: expected and actual don't have the same elements in the same order.");
    }

    @Test
    void findAll_ShouldReturnEmptyList() {
        when(materialRepository.findAll()).thenReturn(Collections.emptyList());

        List<MaterialDto> actualMaterialDtoList = materialService.findAll();

        assertEquals(0, actualMaterialDtoList.size(), "Actual materialDto list is not empty");
    }

    @Test
    void create_ShouldReturnMaterialDto() {
        when(materialRepository.count(any(Specification.class))).thenReturn(0L);
        when(materialRepository.save(any(Material.class))).thenReturn(material);

        MaterialDto actualSavedMaterialDto = materialService.create(materialDto);

        assertNotNull(actualSavedMaterialDto);
        assertNotNull(actualSavedMaterialDto.getId());
        assertEquals(materialDto.getName(), actualSavedMaterialDto.getName(), "MaterialDto name: expected and actual is not equal.");
    }

    @Test
    void create_ShouldThrowException_WhenNameIsNotUnique() {
        when(materialRepository.count(any(Specification.class))).thenReturn(1L);

        assertThrows(EntityDuplicateException.class, () -> materialService.create(materialDto));

        verify(materialRepository, never()).save(any(Material.class));
    }

    @Test
    void update_DataIsCorrect() {
        when(materialRepository.findById(materialDto.getId())).thenReturn(Optional.of(material));

        materialService.update(materialDto.getId(), materialDto);

        verify(materialRepository, times(1)).save(any(Material.class));
    }

    @Test
    void update_ShouldThrowException_WhenIdNotFound() {
        when(materialRepository.findById(materialDto.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> materialService.update(materialDto.getId(), materialDto));

        verify(materialRepository, never()).save(any(Material.class));
    }

    @Test
    void update_ShouldThrowException_WhenNameIsNotUnique() {
        Material materialExisting = new Material(1L, "test1");
        MaterialDto materialDtoToUpdate = new MaterialDto(1L, "test2");

        when(materialRepository.count(any(Specification.class))).thenReturn(1L);
        when(materialRepository.findById(anyLong())).thenReturn(Optional.of(materialExisting));

        assertThrows(EntityDuplicateException.class, () -> materialService.update(materialDtoToUpdate.getId(), materialDtoToUpdate));

        verify(materialRepository, never()).save(any(Material.class));
    }

    @Test
    void deleteById_WhenDataIsCorrect() {
        when(materialRepository.findById(1L)).thenReturn(Optional.of(material));

        materialService.deleteById(1L);

        verify(materialRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteById_ShouldThrowException_WhenIdNotFound() {
        when(materialRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> materialService.deleteById(1L));

        verify(materialRepository, never()).deleteById(anyLong());
    }
}
