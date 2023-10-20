package kms.onlinezoostore.unit.services.impl;

import kms.onlinezoostore.dto.AttachedFileDto;
import kms.onlinezoostore.dto.BrandDto;
import kms.onlinezoostore.dto.mappers.BrandMapper;
import kms.onlinezoostore.entities.Brand;
import kms.onlinezoostore.exceptions.EntityDuplicateException;
import kms.onlinezoostore.exceptions.EntityNotFoundException;
import kms.onlinezoostore.exceptions.files.FileNotFoundException;
import kms.onlinezoostore.exceptions.files.FileUploadException;
import kms.onlinezoostore.repositories.BrandRepository;
import kms.onlinezoostore.services.files.images.AttachedImageOwner;
import kms.onlinezoostore.services.files.images.AttachedImageService;
import kms.onlinezoostore.services.impl.BrandServiceImpl;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;

@ExtendWith(MockitoExtension.class)
class BrandServiceImplTest {

    @Mock
    private BrandRepository brandRepository;
    @Mock
    private AttachedImageService attachedImageService;
    @Spy
    private UniqueFieldService uniqueFieldService = new UniqueFieldServiceImpl();
    @Spy
    private BrandMapper brandMapper = Mappers.getMapper(BrandMapper.class);
    @InjectMocks
    private BrandServiceImpl brandService;

    private Brand brand;
    private BrandDto brandDto;

    @BeforeEach
    public void setup() {
        brand = new Brand(1L, "test1");
        brandDto = brandMapper.mapToDto(brand);
    }

    @Test
    void findById_ShouldReturnBrandDto_WhenIdExists() {
        when(brandRepository.findById(brand.getId())).thenReturn(Optional.of(brand));

        BrandDto brandDtoActual = brandService.findById(brand.getId());

        assertEquals(brandDto, brandDtoActual, "BrandDto: expected and actual is not equal.");
    }

    @Test
    void findById_ShouldThrowException_WhenIdNotFound() {
        when(brandRepository.findById(brand.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> brandService.findById(brand.getId()));
    }

    @Test
    void findAll_ShouldReturnBrandList() {
        List<Brand> brandList = new ArrayList<>(){{ add(brand); }};
        brandList.add(new Brand(2L, "test2"));
        brandList.add(new Brand(3L, "test3"));

        List<BrandDto> brandDtoList = brandList.stream().map(brandMapper::mapToDto).collect(Collectors.toList());

        when(brandRepository.findAll()).thenReturn(brandList);

        // act
        List<BrandDto> actualBrandDtoList = brandService.findAll();

        assertEquals(brandDtoList.size(), actualBrandDtoList.size(), "BrandDto list size: expected and actual is not equal.");
        assertIterableEquals(brandDtoList, actualBrandDtoList, "BrandDto list: expected and actual don't have the same elements in the same order.");
    }

    @Test
    void findAll_ShouldReturnEmptyList() {
        when(brandRepository.findAll()).thenReturn(Collections.emptyList());

        List<BrandDto> actualBrandDtoList = brandService.findAll();

        assertEquals(0, actualBrandDtoList.size(), "Actual brandDto list is not empty");
    }

    @Test
    void create_ShouldReturnBrandDto() {
        when(brandRepository.count(any(Specification.class))).thenReturn(0L);
        when(brandRepository.save(any(Brand.class))).thenReturn(brand);

        BrandDto actualSavedBrandDto = brandService.create(brandDto);

        assertNotNull(actualSavedBrandDto);
        assertNotNull(actualSavedBrandDto.getId());
        assertEquals(brandDto.getName(), actualSavedBrandDto.getName(), "BrandDto name: expected and actual is not equal.");
        assertNull(actualSavedBrandDto.getImage(), "BrandDto image: expected value is not null.");
    }

    @Test
    void create_ShouldThrowException_WhenNameIsNotUnique() {
        when(brandRepository.count(any(Specification.class))).thenReturn(1L);

        assertThrows(EntityDuplicateException.class, () -> brandService.create(brandDto));

        verify(brandRepository, never()).save(any(Brand.class));
    }

    @Test
    void update_DataIsCorrect() {
        when(brandRepository.findById(brandDto.getId())).thenReturn(Optional.of(brand));

        brandService.update(brandDto.getId(), brandDto);

        verify(brandRepository, times(1)).save(any(Brand.class));
    }

    @Test
    void update_ShouldThrowException_WhenIdNotFound() {
        when(brandRepository.findById(brandDto.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> brandService.update(brandDto.getId(), brandDto));

        verify(brandRepository, never()).save(any(Brand.class));
    }

    @Test
    void update_ShouldThrowException_WhenNameIsNotUnique() {
        Brand brandExisting = new Brand(1L, "test1");
        BrandDto brandDtoToUpdate = new BrandDto(1L, "test2", null);

        when(brandRepository.count(any(Specification.class))).thenReturn(1L);
        when(brandRepository.findById(anyLong())).thenReturn(Optional.of(brandExisting));

        assertThrows(EntityDuplicateException.class, () -> brandService.update(brandDtoToUpdate.getId(), brandDtoToUpdate));

        verify(brandRepository, never()).save(any(Brand.class));
    }

    @Test
    void deleteById_WhenDataIsCorrect() {
        when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));

        brandService.deleteById(1L);

        verify(brandRepository, times(1)).deleteById(1L);
        verify(attachedImageService, times(1)).deleteAllByOwner(brandDto);
    }

    @Test
    void deleteById_ShouldThrowException_WhenIdNotFound() {
        when(brandRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> brandService.deleteById(anyLong()));

        verify(brandRepository, never()).deleteById(anyLong());
        verify(attachedImageService, never()).deleteAllByOwner(any(AttachedImageOwner.class));
    }

    /////////////////////
    // ADD INNER CLASS //
    /////////////////////

    @Test
    void uploadImageByOwnerId_ShouldThrowException_WhenOwnerNotFoundById() {
        MultipartFile multipartFile = new MockMultipartFile("testName", new byte[1]);

        when(brandRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> brandService.uploadImageByOwnerId(1L, multipartFile));

        verify(attachedImageService, never()).deleteAllByOwner(any(AttachedImageOwner.class));
        verify(attachedImageService, never()).uploadFileByOwner(any(AttachedImageOwner.class), any(MultipartFile.class));
    }

    @Test
    void uploadImageByOwnerId_ShouldThrowException_WhenImageNotUpload() {
        MultipartFile multipartFile = new MockMultipartFile("testName", new byte[1]);

        when(brandRepository.findById(brand.getId())).thenReturn(Optional.of(brand));
        doThrow(FileNotFoundException.class).when(attachedImageService).deleteAllByOwner(brandDto);

        assertThrows(FileUploadException.class, () -> brandService.uploadImageByOwnerId(brand.getId(), multipartFile));

        verify(attachedImageService, never()).uploadFileByOwner(brandDto, multipartFile);
    }

    @Test
    void uploadImageByOwnerId_ShouldReturnAttachedFileDto_WhenDataIsCorrect() {
        MultipartFile multipartFile = new MockMultipartFile("testName", new byte[1]);
        AttachedFileDto imageDto = new AttachedFileDto(brand.getId(), "testPath", "testName");

        when(brandRepository.findById(brand.getId())).thenReturn(Optional.of(brand));
        doNothing().when(attachedImageService).deleteAllByOwner(brandDto);
        when(attachedImageService.uploadFileByOwner(brandDto, multipartFile)).thenReturn(imageDto);

        AttachedFileDto actualImageDto = brandService.uploadImageByOwnerId(brand.getId(), multipartFile);

        verify(attachedImageService, times(1)).uploadFileByOwner(brandDto, multipartFile);

        assertNotNull(actualImageDto);
        assertNotNull(actualImageDto.getId());
        assertNotNull(actualImageDto.getFilePath());
        assertEquals(multipartFile.getName(), actualImageDto.getFileName(), "AttachedFileDto name: expected and actual is not equal.");
    }

    @Test
    void deleteImageByOwnerId_ShouldThrowException_WhenOwnerNotFoundById() {
        when(brandRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> brandService.deleteImageByOwnerId(1L));

        verify(attachedImageService, never()).deleteAllByOwner(any(AttachedImageOwner.class));
    }

    @Test
    void deleteImageByOwnerId_WhenDataIsCorrect() {
        when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));

        brandService.deleteImageByOwnerId(1L);

        verify(attachedImageService, times(1)).deleteAllByOwner(brandDto);
    }
}
