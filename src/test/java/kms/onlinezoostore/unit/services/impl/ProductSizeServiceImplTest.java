package kms.onlinezoostore.unit.services.impl;

import kms.onlinezoostore.dto.ProductSizeDto;
import kms.onlinezoostore.dto.mappers.ProductSizeMapper;
import kms.onlinezoostore.entities.ProductSize;
import kms.onlinezoostore.exceptions.EntityDuplicateException;
import kms.onlinezoostore.exceptions.EntityNotFoundException;
import kms.onlinezoostore.repositories.ProductSizeRepository;
import kms.onlinezoostore.services.impl.ProductSizeServiceImpl;
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
class ProductSizeServiceImplTest {

    @Mock
    private ProductSizeRepository productSizeRepository;
    @Spy
    private UniqueFieldService uniqueFieldService = new UniqueFieldServiceImpl();
    @Spy
    private ProductSizeMapper productSizeMapper = Mappers.getMapper(ProductSizeMapper.class);
    @InjectMocks
    private ProductSizeServiceImpl productSizeService;

    private ProductSize productSize;
    private ProductSizeDto productSizeDto;

    @BeforeEach
    public void setup() {
        productSize = new ProductSize(1L, "test1");
        productSizeDto = productSizeMapper.mapToDto(productSize);
    }

    @Test
    void findById_ShouldReturnProductSizeDto_WhenIdExists() {
        when(productSizeRepository.findById(productSize.getId())).thenReturn(Optional.of(productSize));

        ProductSizeDto productSizeDtoActual = productSizeService.findById(productSize.getId());

        assertEquals(productSizeDto, productSizeDtoActual, "ProductSizeDto: expected and actual is not equal.");
    }

    @Test
    void findById_ShouldThrowException_WhenIdNotFound() {
        when(productSizeRepository.findById(productSize.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productSizeService.findById(productSize.getId()));
    }

    @Test
    void findAll_ShouldReturnProductSizeList() {
        List<ProductSize> productSizeList = new ArrayList<>(){{ add(productSize); }};
        productSizeList.add(new ProductSize(2L, "test2"));
        productSizeList.add(new ProductSize(3L, "test3"));

        List<ProductSizeDto> productSizeDtoList = productSizeList.stream().map(productSizeMapper::mapToDto).collect(Collectors.toList());

        when(productSizeRepository.findAll()).thenReturn(productSizeList);

        // act
        List<ProductSizeDto> actualProductSizeDtoList = productSizeService.findAll();

        assertEquals(productSizeDtoList.size(), actualProductSizeDtoList.size(), "ProductSizeDto list size: expected and actual is not equal.");
        assertIterableEquals(productSizeDtoList, actualProductSizeDtoList, "ProductSizeDto list: expected and actual don't have the same elements in the same order.");
    }

    @Test
    void findAll_ShouldReturnEmptyList() {
        when(productSizeRepository.findAll()).thenReturn(Collections.emptyList());

        List<ProductSizeDto> actualProductSizeDtoList = productSizeService.findAll();

        assertEquals(0, actualProductSizeDtoList.size(), "Actual productSizeDto list is not empty");
    }

    @Test
    void create_ShouldReturnProductSizeDto() {
        when(productSizeRepository.count(any(Specification.class))).thenReturn(0L);
        when(productSizeRepository.save(any(ProductSize.class))).thenReturn(productSize);

        ProductSizeDto actualSavedProductSizeDto = productSizeService.create(productSizeDto);

        assertNotNull(actualSavedProductSizeDto);
        assertNotNull(actualSavedProductSizeDto.getId());
        assertEquals(productSizeDto.getName(), actualSavedProductSizeDto.getName(), "ProductSizeDto name: expected and actual is not equal.");
    }

    @Test
    void create_ShouldThrowException_WhenNameIsNotUnique() {
        when(productSizeRepository.count(any(Specification.class))).thenReturn(1L);

        assertThrows(EntityDuplicateException.class, () -> productSizeService.create(productSizeDto));

        verify(productSizeRepository, never()).save(any(ProductSize.class));
    }

    @Test
    void update_DataIsCorrect() {
        when(productSizeRepository.findById(productSizeDto.getId())).thenReturn(Optional.of(productSize));

        productSizeService.update(productSizeDto.getId(), productSizeDto);

        verify(productSizeRepository, times(1)).save(any(ProductSize.class));
    }

    @Test
    void update_ShouldThrowException_WhenIdNotFound() {
        when(productSizeRepository.findById(productSizeDto.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productSizeService.update(productSizeDto.getId(), productSizeDto));

        verify(productSizeRepository, never()).save(any(ProductSize.class));
    }

    @Test
    void update_ShouldThrowException_WhenNameIsNotUnique() {
        ProductSize productSizeExisting = new ProductSize(1L, "test1");
        ProductSizeDto productSizeDtoToUpdate = new ProductSizeDto(1L, "test2");

        when(productSizeRepository.count(any(Specification.class))).thenReturn(1L);
        when(productSizeRepository.findById(anyLong())).thenReturn(Optional.of(productSizeExisting));

        assertThrows(EntityDuplicateException.class, () -> productSizeService.update(productSizeDtoToUpdate.getId(), productSizeDtoToUpdate));

        verify(productSizeRepository, never()).save(any(ProductSize.class));
    }

    @Test
    void deleteById_WhenDataIsCorrect() {
        when(productSizeRepository.findById(1L)).thenReturn(Optional.of(productSize));

        productSizeService.deleteById(1L);

        verify(productSizeRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteById_ShouldThrowException_WhenIdNotFound() {
        when(productSizeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productSizeService.deleteById(1L));

        verify(productSizeRepository, never()).deleteById(anyLong());
    }
}
