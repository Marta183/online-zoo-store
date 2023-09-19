package kms.onlinezoostore.unit.services.impl;

import kms.onlinezoostore.dto.AttachedFileDto;
import kms.onlinezoostore.dto.ProductCategoryDto;
import kms.onlinezoostore.dto.mappers.ProductCategoryMapper;
import kms.onlinezoostore.entities.ProductCategory;
import kms.onlinezoostore.exceptions.EntityCannotBeDeleted;
import kms.onlinezoostore.exceptions.EntityDuplicateException;
import kms.onlinezoostore.exceptions.EntityNotFoundException;
import kms.onlinezoostore.exceptions.files.FileNotFoundException;
import kms.onlinezoostore.exceptions.files.FileUploadException;
import kms.onlinezoostore.repositories.ProductCategoryRepository;
import kms.onlinezoostore.repositories.ProductRepository;
import kms.onlinezoostore.services.files.images.AttachedImageOwner;
import kms.onlinezoostore.services.files.images.AttachedImageService;
import kms.onlinezoostore.services.impl.ProductCategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.Objects;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import static org.mockito.Mockito.anyString;

@ExtendWith(MockitoExtension.class)
class ProductCategoryServiceImplTest {

    @Mock
    private ProductCategoryRepository categoryRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private AttachedImageService attachedImageService;
    @Spy
    private ProductCategoryMapper categoryMapper = Mappers.getMapper(ProductCategoryMapper.class);
    @InjectMocks
    private ProductCategoryServiceImpl categoryService;

    private ProductCategory categoryParent;
    private ProductCategory categoryInner;
    private ProductCategoryDto categoryParentDto;
    private ProductCategoryDto categoryInnerDto;

    @BeforeEach
    public void setup() {
        categoryParent = new ProductCategory(1L, "test1", null);
        categoryInner = new ProductCategory(2L, "test2", categoryParent);
        categoryParent.setInnerCategories(Set.of(categoryInner));
        categoryParentDto = categoryMapper.mapToDto(categoryParent);
        categoryInnerDto = categoryMapper.mapToDto(categoryInner);
    }

    @Test
    void findById_ShouldReturnProductCategoryDto_WhenIdExists() {
        when(categoryRepository.findById(categoryParent.getId())).thenReturn(Optional.of(categoryParent));

        ProductCategoryDto categoryParentDtoActual = categoryService.findById(categoryParent.getId());

        assertEquals(categoryParentDto, categoryParentDtoActual, "ProductCategoryDto: expected and actual is not equal.");
    }

    @Test
    void findById_ShouldThrowException_WhenIdNotFound() {
        when(categoryRepository.findById(categoryParent.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoryService.findById(categoryParent.getId()));
    }

    @Test
    void findAll_ShouldReturnProductCategoryList() {
        List<ProductCategory> categoryList = getCategoryList();

        List<ProductCategoryDto> expectedCategoryDtoList = categoryList.stream()
                .map((el) -> categoryMapper.mapToDto(el))
                .collect(Collectors.toList());

        when(categoryRepository.findAll()).thenReturn(categoryList);

        // act
        List<ProductCategoryDto> actualProductCategoryDtoList = categoryService.findAll();

        assertEquals(expectedCategoryDtoList.size(), actualProductCategoryDtoList.size(), "ProductCategoryDto list size: expected and actual is not equal.");
        assertIterableEquals(expectedCategoryDtoList, actualProductCategoryDtoList, "ProductCategoryDto list: expected and actual don't have the same elements in the same order.");
    }

    @Test
    void findAll_ShouldReturnEmptyList() {
        when(categoryRepository.findAll()).thenReturn(Collections.emptyList());

        List<ProductCategoryDto> actualProductCategoryDtoList = categoryService.findAll();

        assertEquals(0, actualProductCategoryDtoList.size(), "Actual categoryParentDto list is not empty");
    }

    @Test
    void findAllByNameLike_ShouldReturnProductCategoryList() {
        List<ProductCategory> categoryList = new ArrayList<>(){{ add(categoryParent); add(categoryInner); }};
        categoryList.add(new ProductCategory(3L, "test3", null));
        categoryList.add(new ProductCategory(4L, "test4", null));

        List<ProductCategoryDto> expectedCategoryDtoList = categoryList.stream()
                .map(categoryMapper::mapToDto)
                .collect(Collectors.toList());

        when(categoryRepository.findAllByNameContainsIgnoreCase(anyString())).thenReturn(categoryList);

        // act
        List<ProductCategoryDto> actualCategoryDtoList = categoryService.findAllByNameLike("Test");

        assertEquals(expectedCategoryDtoList.size(), actualCategoryDtoList.size(), "ProductCategoryDto list size: expected and actual is not equal.");
        assertIterableEquals(expectedCategoryDtoList, actualCategoryDtoList, "ProductCategoryDto list: expected and actual don't have the same elements in the same order.");
    }

    @Test
    void findAllByNameLike_ShouldReturnEmptyList() {
        when(categoryRepository.findAllByNameContainsIgnoreCase(anyString())).thenReturn(Collections.emptyList());

        List<ProductCategoryDto> actualProductCategoryDtoList = categoryService.findAllByNameLike("");

        assertEquals(0, actualProductCategoryDtoList.size(), "Actual categoryParentDto list is not empty");
    }

    @Test
    void findAllByParentId_ShouldReturnProductCategoryList() {
        Long parentId = categoryParent.getId();
        List<ProductCategory> categoryList = new ArrayList<>() {{ add(categoryInner); }};
        List<ProductCategoryDto> expectedList = categoryList.stream().map(categoryMapper::mapToDto).collect(Collectors.toList());

        when(categoryRepository.findById(parentId)).thenReturn(Optional.of(categoryParent));

        // act
        List<ProductCategoryDto> actualList = categoryService.findAllByParentId(parentId);

        assertNotNull(actualList);
        assertEquals(expectedList.size(), actualList.size(), "ProductCategoryDto list size: expected and actual is not equal.");
        assertIterableEquals(expectedList, actualList, "ProductCategoryDto list: expected and actual don't have the same elements in the same order.");
    }

    @ParameterizedTest
    @MethodSource("getCategoriesStream")
    void create_ShouldReturnProductCategoryDto_WhenDataIsCorrect(ProductCategory category) {
        ProductCategory parent = category.getParent();
        if (Objects.isNull(parent)) {
            when(categoryRepository.countAllByParentIdAndNameIgnoreCase(null, category.getName())).thenReturn(0L);
        } else {
            when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(parent));
            when(categoryRepository.countAllByParentIdAndNameIgnoreCase(parent.getId(), category.getName())).thenReturn(0L);
        }
        when(categoryRepository.save(any(ProductCategory.class))).thenReturn(category);

        // act
        ProductCategoryDto actualCategoryDto = categoryService.create(categoryMapper.mapToDto(category));

        assertNotNull(actualCategoryDto);
        assertNotNull(actualCategoryDto.getId());
        assertEquals(category.getName(), actualCategoryDto.getName(), "ProductCategoryDto name: expected and actual is not equal.");
        assertEquals(categoryMapper.mapToDto(parent), actualCategoryDto.getParent(), "ProductCategoryDto parent: expected and actual is not equal.");
    }

    @ParameterizedTest
    @MethodSource("getCategoriesStream")
    void create_ShouldThrowException_WhenNameIsNotUnique(ProductCategory category) {
        ProductCategory parent = category.getParent();
        if (Objects.isNull(parent)) {
            when(categoryRepository.countAllByParentIdAndNameIgnoreCase(null, category.getName())).thenReturn(1L);
        } else {
            when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(parent));
            when(categoryRepository.countAllByParentIdAndNameIgnoreCase(parent.getId(), category.getName())).thenReturn(1L);
        }

        assertThrows(EntityDuplicateException.class, () -> categoryService.create(categoryMapper.mapToDto(category)));

        verify(categoryRepository, never()).save(any(ProductCategory.class));
    }

    @Test
    void create_ShouldThrowException_WhenParentNotFoundById() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoryService.create(categoryInnerDto));

        verify(categoryRepository, never()).save(any(ProductCategory.class));
    }

    @ParameterizedTest
    @MethodSource("getCategoriesStream")
    void update_DataIsCorrect(ProductCategory category) {
        ProductCategory parent = category.getParent();
        ProductCategoryDto parentDto = categoryMapper.mapToDto(category.getParent());
        ProductCategoryDto categoryDtoToUpdate = new ProductCategoryDto(category.getId(), category.getName() + "/", parentDto);

        if (Objects.isNull(parent)) {
            when(categoryRepository.countAllByParentIdAndNameIgnoreCase(null, categoryDtoToUpdate.getName())).thenReturn(0L);
        } else {
            when(categoryRepository.findById(parent.getId())).thenReturn(Optional.of(parent));
            when(categoryRepository.countAllByParentIdAndNameIgnoreCase(parent.getId(), categoryDtoToUpdate.getName())).thenReturn(0L);
        }
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));

        // act
        categoryService.update(category.getId(), categoryDtoToUpdate);

        verify(categoryRepository, times(1)).save(any(ProductCategory.class));
    }

    @Test
    void update_ShouldThrowException_WhenIdNotFound() {
        when(categoryRepository.findById(categoryInnerDto.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoryService.update(categoryInnerDto.getId(), categoryInnerDto));

        verify(categoryRepository, never()).save(any(ProductCategory.class));
    }

    @Test
    void update_ShouldThrowException_WhenParentNotFoundById() {
        when(categoryRepository.findById(categoryInnerDto.getId())).thenReturn(Optional.of(categoryInner));
        when(categoryRepository.findById(categoryInnerDto.getParent().getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoryService.update(categoryInnerDto.getId(), categoryInnerDto));

        verify(categoryRepository, never()).save(any(ProductCategory.class));
    }

    @ParameterizedTest
    @MethodSource("getCategoriesStream")
    void update_ShouldThrowException_WhenNameIsNotUnique(ProductCategory category) {
        ProductCategory parent = category.getParent();
        ProductCategoryDto parentDto = categoryMapper.mapToDto(category.getParent());
        ProductCategoryDto categoryDtoToUpdate = new ProductCategoryDto(category.getId(), category.getName() + "/", parentDto);

        if (Objects.isNull(parent)) {
            when(categoryRepository.countAllByParentIdAndNameIgnoreCase(null, categoryDtoToUpdate.getName())).thenReturn(1L);
        } else {
            when(categoryRepository.findById(parent.getId())).thenReturn(Optional.of(parent));
            when(categoryRepository.countAllByParentIdAndNameIgnoreCase(parent.getId(), categoryDtoToUpdate.getName())).thenReturn(1L);
        }
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));

        // act
        assertThrows(EntityDuplicateException.class, () -> categoryService.update(category.getId(), categoryDtoToUpdate));

        verify(categoryRepository, never()).save(any(ProductCategory.class));
    }

    @Test
    void deleteById_WhenDataIsCorrect() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(categoryInner));
        when(productRepository.countByCategoryId(1L)).thenReturn(0L);
        when(categoryRepository.findInnerCategoriesWithProductCountByParentId(categoryParent.getId())).thenReturn(Collections.emptyList());

        categoryService.deleteById(1L);

        verify(categoryRepository, times(1)).deleteById(1L);
        verify(attachedImageService, times(1)).deleteAllByOwner(categoryInnerDto);
    }

    @Test
    void deleteById_ShouldThrowException_WhenIdNotFound() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoryService.deleteById(anyLong()));

        verify(categoryRepository, never()).deleteById(anyLong());
        verify(attachedImageService, never()).deleteAllByOwner(any(AttachedImageOwner.class));
    }

    @Test
    void deleteById_ShouldThrowException_WhenExistsAnyProductByCategoryId() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(categoryInner));
        when(productRepository.countByCategoryId(anyLong())).thenReturn(1L);

        assertThrows(EntityCannotBeDeleted.class, () -> categoryService.deleteById(anyLong()));

        verify(categoryRepository, never()).deleteById(anyLong());
        verify(attachedImageService, never()).deleteAllByOwner(any(AttachedImageOwner.class));
    }

    @Test
    void deleteById_ShouldThrowException_WhenExistsAnyProductForInnerCategories() {
        ProductCategory categoryWithProducts = new ProductCategory(5L, "test5", categoryParent, 1L);
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(categoryParent));
        when(productRepository.countByCategoryId(anyLong())).thenReturn(0L);
        when(categoryRepository.findInnerCategoriesWithProductCountByParentId(categoryParent.getId()))
                .thenReturn(List.of(categoryWithProducts));

        assertThrows(EntityCannotBeDeleted.class, () -> categoryService.deleteById(categoryParent.getId()));

        verify(categoryRepository, never()).deleteById(anyLong());
        verify(attachedImageService, never()).deleteAllByOwner(any(AttachedImageOwner.class));
    }

    private static Stream<ProductCategory> getCategoriesStream() {
        return getCategoryList().stream();
    }
    private static List<ProductCategory> getCategoryList() {
        ProductCategory categoryParent = new ProductCategory(1L, "test1", null);
        ProductCategory categoryInnerParent = new ProductCategory(2L, "test2", categoryParent);

        List<ProductCategory> categoryList = new ArrayList<>();
        categoryList.add(categoryParent);
        categoryList.add(categoryInnerParent);
        categoryList.add(new ProductCategory(3L, "test3", categoryParent));
        categoryList.add(new ProductCategory(4L, "test4", categoryParent));
        categoryList.add(new ProductCategory(5L, "test5", categoryInnerParent));
        categoryList.add(new ProductCategory(6L, "test6", categoryInnerParent));
        categoryList.add(new ProductCategory(7L, "test7", null));
        categoryList.add(new ProductCategory(8L, "test7", null));

        return categoryList;
    }

    /////////////////////
    // ADD INNER CLASS //
    /////////////////////

    @Test
    void findImageByOwnerId_ShouldThrowException_WhenOwnerNotFoundById() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoryService.findImageByOwnerId(1L));

        verify(attachedImageService, never()).findFirstByOwner(any(AttachedImageOwner.class));
    }

    @Test
    void findImageByOwnerId_ShouldReturnNull_WhenImageNotFound() {
        when(categoryRepository.findById(categoryInner.getId())).thenReturn(Optional.of(categoryInner));
        when(attachedImageService.findFirstByOwner(categoryInnerDto)).thenReturn(null);

        AttachedFileDto actualImageDto = categoryService.findImageByOwnerId(categoryInner.getId());

        assertNull(actualImageDto, "ImageDto is not null");
    }

    @Test
    void findImageByOwnerId_ShouldReturnAttachedFileDto_WhenImageExists() {
        AttachedFileDto imageDto = new AttachedFileDto(1L, "testPath", "testName");

        when(categoryRepository.findById(categoryInner.getId())).thenReturn(Optional.of(categoryInner));
        when(attachedImageService.findFirstByOwner(categoryInnerDto)).thenReturn(imageDto);

        AttachedFileDto actualImageDto = categoryService.findImageByOwnerId(categoryInner.getId());

        assertEquals(imageDto, actualImageDto, "ImageDto: expected and actual is not equal.");
    }

    @Test
    void uploadImageByOwnerId_ShouldThrowException_WhenOwnerNotFoundById() {
        MultipartFile multipartFile = new MockMultipartFile("testName", new byte[1]);

        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoryService.uploadImageByOwnerId(1L, multipartFile));

        verify(attachedImageService, never()).deleteAllByOwner(any(AttachedImageOwner.class));
        verify(attachedImageService, never()).uploadFileByOwner(any(AttachedImageOwner.class), any(MultipartFile.class));
    }

    @Test
    void uploadImageByOwnerId_ShouldThrowException_WhenImageNotUpload() {
        MultipartFile multipartFile = new MockMultipartFile("testName", new byte[1]);

        when(categoryRepository.findById(categoryInner.getId())).thenReturn(Optional.of(categoryInner));
        doThrow(FileNotFoundException.class).when(attachedImageService).deleteAllByOwner(categoryInnerDto);

        assertThrows(FileUploadException.class, () -> categoryService.uploadImageByOwnerId(categoryInner.getId(), multipartFile));

        verify(attachedImageService, never()).uploadFileByOwner(categoryInnerDto, multipartFile);
    }

    @Test
    void uploadImageByOwnerId_ShouldReturnAttachedFileDto_WhenDataIsCorrect() {
        MultipartFile multipartFile = new MockMultipartFile("testName", new byte[1]);
        AttachedFileDto imageDto = new AttachedFileDto(1L, "testPath", "testName");

        when(categoryRepository.findById(categoryInner.getId())).thenReturn(Optional.of(categoryInner));
        doNothing().when(attachedImageService).deleteAllByOwner(categoryInnerDto);
        when(attachedImageService.uploadFileByOwner(categoryInnerDto, multipartFile)).thenReturn(imageDto);

        AttachedFileDto actualImageDto = categoryService.uploadImageByOwnerId(categoryInner.getId(), multipartFile);

        verify(attachedImageService, times(1)).uploadFileByOwner(categoryInnerDto, multipartFile);

        assertNotNull(actualImageDto);
        assertNotNull(actualImageDto.getId());
        assertNotNull(actualImageDto.getFilePath());
        assertEquals(multipartFile.getName(), actualImageDto.getFileName(), "AttachedFileDto name: expected and actual is not equal.");
    }

    @Test
    void deleteImageByOwnerId_ShouldThrowException_WhenOwnerNotFoundById() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoryService.deleteImageByOwnerId(1L));

        verify(attachedImageService, never()).deleteAllByOwner(any(AttachedImageOwner.class));
    }

    @Test
    void deleteImageByOwnerId_WhenDataIsCorrect() {
        when(categoryRepository.findById(categoryInner.getId())).thenReturn(Optional.of(categoryInner));

        categoryService.deleteImageByOwnerId(categoryInner.getId());

        verify(attachedImageService, times(1)).deleteAllByOwner(categoryInnerDto);
    }
}
