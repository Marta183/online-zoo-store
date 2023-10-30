package kms.onlinezoostore.unit.services.impl;

import kms.onlinezoostore.dto.AttachedFileDto;
import kms.onlinezoostore.dto.ConstantDto;
import kms.onlinezoostore.dto.mappers.AttachedFileMapper;
import kms.onlinezoostore.dto.mappers.ConstantMapper;
import kms.onlinezoostore.entities.AttachedFile;
import kms.onlinezoostore.entities.Constant;
import kms.onlinezoostore.entities.enums.ConstantKeys;
import kms.onlinezoostore.exceptions.EntityCannotBeUpdated;
import kms.onlinezoostore.exceptions.EntityNotFoundException;
import kms.onlinezoostore.repositories.ConstantRepository;
import kms.onlinezoostore.services.files.images.AttachedImageService;
import kms.onlinezoostore.services.impl.ConstantServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.only;

@ExtendWith(MockitoExtension.class)
class ConstantServiceImplTest {

    @Mock
    private ConstantRepository constantRepository;
    @Mock
    private AttachedImageService attachedImageService;
    @Spy
    private ConstantMapper constantMapper = Mappers.getMapper(ConstantMapper.class);
    @Spy
    private AttachedFileMapper attachedFileMapper = Mappers.getMapper(AttachedFileMapper.class);
    @InjectMocks
    private ConstantServiceImpl constantService;

    @Test
    void findAll_ShouldReturnConstantList() {
        List<Constant> constantList = new ArrayList<>() {{
            add(new Constant(1L, ConstantKeys.LOGO, "11", false, null));
            add(new Constant(1L, ConstantKeys.CURRENCY, "22", true, null));
        }};
        List<ConstantDto> expectedConstantDtoList = constantList.stream().map(constantMapper::mapToDto).collect(Collectors.toList());

        when(constantRepository.findAll()).thenReturn(constantList);

        // act
        List<ConstantDto> actualConstantDtoList = constantService.findAll();

        assertEquals(expectedConstantDtoList.size(), actualConstantDtoList.size(), "ConstantDto list size: expected and actual is not equal.");
        assertIterableEquals(expectedConstantDtoList, actualConstantDtoList, "ConstantDto list: expected and actual don't have the same elements in the same order.");
    }

    @Test
    void findAll_ShouldReturnEmptyList() {
        when(constantRepository.findAll()).thenReturn(Collections.emptyList());

        List<ConstantDto> actualConstantDtoList = constantService.findAll();

        assertEquals(0, actualConstantDtoList.size(), "Actual ConstantDto list is not empty");
    }

    @Test
    void findByKey_ShouldReturnConstantDto_WhenKeyExists() {
        Constant currency = new Constant(1L, ConstantKeys.CURRENCY, "test", false, null);
        ConstantDto expectedConstantDto = constantMapper.mapToDto(currency);

        when(constantRepository.findByKey(ConstantKeys.CURRENCY)).thenReturn(Optional.of(currency));

        ConstantDto actualConstantDto = constantService.findByKey(ConstantKeys.CURRENCY);

        assertEquals(expectedConstantDto, actualConstantDto, "ConstantDto: expected and actual is not equal.");
    }

    @Test
    void findByKey_ShouldThrowException_WhenKeyNotFound() {
        when(constantRepository.findByKey(any(ConstantKeys.class))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> constantService.findByKey(ConstantKeys.CURRENCY));
    }

    @Test
    void updateValue_ShouldThrowException_WhenKeyNotFound() {
        when(constantRepository.findByKey(any(ConstantKeys.class))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> constantService.updateValue(ConstantKeys.CURRENCY, "test"));
    }

    @Test
    void updateValue_ShouldReturnConstantDto_WhenValueIsString() {
        Object newValue = new String("new");
        Constant existing = new Constant(1L, ConstantKeys.CURRENCY, "old", false, null);
        ConstantDto expected = new ConstantDto(1L, ConstantKeys.CURRENCY, "new");

        when(constantRepository.findByKey(ConstantKeys.CURRENCY)).thenReturn(Optional.of(existing));

        ConstantDto actual = constantService.updateValue(ConstantKeys.CURRENCY, newValue);

        assertEquals(expected, actual, "Constant: expected and actual value is not equal.");
    }

    @Test
    void updateValue_ShouldReturnConstantDto_WhenValueIsNullForString() {
        Constant currency = new Constant(1L, ConstantKeys.CURRENCY, "test", false, null);

        when(constantRepository.findByKey(ConstantKeys.CURRENCY)).thenReturn(Optional.of(currency));

        constantService.updateValue(ConstantKeys.CURRENCY, null);

        assertNull(currency.getValue(), "Constant: actual value is not null.");
    }

    @Test
    void updateValue_ShouldThrowException_WhenValueTypeIsUnsupportable() {
        Constant currency = new Constant(1L, ConstantKeys.CURRENCY, "test", false, null);

        when(constantRepository.findByKey(ConstantKeys.CURRENCY)).thenReturn(Optional.of(currency));

        assertThrows(EntityCannotBeUpdated.class, () -> constantService.updateValue(ConstantKeys.CURRENCY, 11));
    }

    @Test
    void updateValue_ShouldReturnConstantDto_WhenValueIsNullForImage() {
        Constant logo = new Constant(1L, ConstantKeys.LOGO, "1L", true, Collections.singletonList(new AttachedFile()));
        ConstantDto logoDto = constantMapper.mapToDto(logo);

        when(constantRepository.findByKey(ConstantKeys.LOGO)).thenReturn(Optional.of(logo));
        doNothing().when(attachedImageService).deleteAllByOwner(logoDto);

        constantService.updateValue(ConstantKeys.LOGO, null);

        assertNull(logo.getValue(), "Constant: actual value is not null.");

        verify(attachedImageService, only()).deleteAllByOwner(logoDto);
    }

    @Test
    void updateValue_ShouldReturnConstantDto_WhenValueIsNewImage() {
        AttachedFile oldImage = new AttachedFile(2L, "oldPath", "oldName", 1L, "Constant");
        Constant existing = new Constant(1L, ConstantKeys.LOGO, "1L", true, Collections.singletonList(oldImage));
        ConstantDto existingDto = constantMapper.mapToDto(existing);
        MultipartFile multipartFile = new MockMultipartFile("newName", new byte[1]);
        AttachedFileDto newImageDto = new AttachedFileDto(3L, "newPath", "newName");
        ConstantDto expected = new ConstantDto(1L, ConstantKeys.LOGO, newImageDto);

        when(constantRepository.findByKey(ConstantKeys.LOGO)).thenReturn(Optional.of(existing));
        doNothing().when(attachedImageService).deleteAllByOwner(existingDto);
        when(attachedImageService.uploadFileByOwner(existingDto, multipartFile)).thenReturn(newImageDto);

        // act
        ConstantDto actual = constantService.updateValue(ConstantKeys.LOGO, multipartFile);

        assertNotNull(actual);
        assertEquals(expected, actual, "Constant: expected and actual is not equal.");

        verify(attachedImageService, times(1)).deleteAllByOwner(existingDto);
        verify(attachedImageService, times(1)).uploadFileByOwner(existingDto, multipartFile);
    }
}