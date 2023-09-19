package kms.onlinezoostore.unit.services.impl;

import kms.onlinezoostore.dto.ColorDto;
import kms.onlinezoostore.dto.mappers.ColorMapper;
import kms.onlinezoostore.entities.Color;
import kms.onlinezoostore.exceptions.EntityDuplicateException;
import kms.onlinezoostore.exceptions.EntityNotFoundException;
import kms.onlinezoostore.repositories.ColorRepository;
import kms.onlinezoostore.services.impl.ColorServiceImpl;
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
class ColorServiceImplTest {

    @Mock
    private ColorRepository colorRepository;
    @Spy
    private UniqueFieldService uniqueFieldService = new UniqueFieldServiceImpl();
    @Spy
    private ColorMapper colorMapper = Mappers.getMapper(ColorMapper.class);
    @InjectMocks
    private ColorServiceImpl colorService;

    private Color color;
    private ColorDto colorDto;

    @BeforeEach
    public void setup() {
        color = new Color(1L, "test1");
        colorDto = colorMapper.mapToDto(color);
    }

    @Test
    void findById_ShouldReturnColorDto_WhenIdExists() {
        when(colorRepository.findById(color.getId())).thenReturn(Optional.of(color));

        ColorDto colorDtoActual = colorService.findById(color.getId());

        assertEquals(colorDto, colorDtoActual, "ColorDto: expected and actual is not equal.");
    }

    @Test
    void findById_ShouldThrowException_WhenIdNotFound() {
        when(colorRepository.findById(color.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> colorService.findById(color.getId()));
    }

    @Test
    void findAll_ShouldReturnColorList() {
        List<Color> colorList = new ArrayList<>(){{ add(color); }};
        colorList.add(new Color(2L, "test2"));
        colorList.add(new Color(3L, "test3"));

        List<ColorDto> colorDtoList = colorList.stream().map(colorMapper::mapToDto).collect(Collectors.toList());

        when(colorRepository.findAll()).thenReturn(colorList);

        // act
        List<ColorDto> actualColorDtoList = colorService.findAll();

        assertEquals(colorDtoList.size(), actualColorDtoList.size(), "ColorDto list size: expected and actual is not equal.");
        assertIterableEquals(colorDtoList, actualColorDtoList, "ColorDto list: expected and actual don't have the same elements in the same order.");
    }

    @Test
    void findAll_ShouldReturnEmptyList() {
        when(colorRepository.findAll()).thenReturn(Collections.emptyList());

        List<ColorDto> actualColorDtoList = colorService.findAll();

        assertEquals(0, actualColorDtoList.size(), "Actual colorDto list is not empty");
    }

    @Test
    void create_ShouldReturnColorDto() {
        when(colorRepository.count(any(Specification.class))).thenReturn(0L);
        when(colorRepository.save(any(Color.class))).thenReturn(color);

        ColorDto actualSavedColorDto = colorService.create(colorDto);

        assertNotNull(actualSavedColorDto);
        assertNotNull(actualSavedColorDto.getId());
        assertEquals(colorDto.getName(), actualSavedColorDto.getName(), "ColorDto name: expected and actual is not equal.");
    }

    @Test
    void create_ShouldThrowException_WhenNameIsNotUnique() {
        when(colorRepository.count(any(Specification.class))).thenReturn(1L);

        assertThrows(EntityDuplicateException.class, () -> colorService.create(colorDto));

        verify(colorRepository, never()).save(any(Color.class));
    }

    @Test
    void update_DataIsCorrect() {
        when(colorRepository.findById(colorDto.getId())).thenReturn(Optional.of(color));

        colorService.update(colorDto.getId(), colorDto);

        verify(colorRepository, times(1)).save(any(Color.class));
    }

    @Test
    void update_ShouldThrowException_WhenIdNotFound() {
        when(colorRepository.findById(colorDto.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> colorService.update(colorDto.getId(), colorDto));

        verify(colorRepository, never()).save(any(Color.class));
    }

    @Test
    void update_ShouldThrowException_WhenNameIsNotUnique() {
        Color colorExisting = new Color(1L, "test1");
        ColorDto colorDtoToUpdate = new ColorDto(1L, "test2");

        when(colorRepository.count(any(Specification.class))).thenReturn(1L);
        when(colorRepository.findById(anyLong())).thenReturn(Optional.of(colorExisting));

        assertThrows(EntityDuplicateException.class, () -> colorService.update(colorDtoToUpdate.getId(), colorDtoToUpdate));

        verify(colorRepository, never()).save(any(Color.class));
    }

    @Test
    void deleteById_WhenDataIsCorrect() {
        when(colorRepository.findById(1L)).thenReturn(Optional.of(color));

        colorService.deleteById(1L);

        verify(colorRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteById_ShouldThrowException_WhenIdNotFound() {
        when(colorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> colorService.deleteById(1L));

        verify(colorRepository, never()).deleteById(anyLong());
    }
}
