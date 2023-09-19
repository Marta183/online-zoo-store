package kms.onlinezoostore.unit.services.impl;

import kms.onlinezoostore.dto.*;
import kms.onlinezoostore.dto.mappers.AttachedFileMapper;
import kms.onlinezoostore.dto.mappers.ProductMapper;
import kms.onlinezoostore.entities.*;
import kms.onlinezoostore.exceptions.EntityDuplicateException;
import kms.onlinezoostore.exceptions.EntityNotFoundException;
import kms.onlinezoostore.exceptions.PriceConflictException;
import kms.onlinezoostore.repositories.ProductRepository;
import kms.onlinezoostore.services.*;
import kms.onlinezoostore.services.files.images.AttachedImageOwner;
import kms.onlinezoostore.services.files.images.AttachedImageService;
import kms.onlinezoostore.services.impl.ProductServiceImpl;
import kms.onlinezoostore.utils.UniqueFieldService;
import kms.onlinezoostore.utils.UniqueFieldServiceImpl;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;
import java.util.Optional;
import java.util.Objects;
import java.util.List;
import java.util.ArrayList;
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

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private AttachedImageService attachedImageService;
    @Mock
    private AgeService ageService;
    @Mock
    private BrandService brandService;
    @Mock
    private ColorService colorService;
    @Mock
    private MaterialService materialService;
    @Mock
    private PrescriptionService prescriptionService;
    @Mock
    private ProductCategoryService productCategoryService;
    @Mock
    private ProductSizeService productSizeService;
    @Mock
    private WeightService weightService;
    @Spy
    private ProductMapper productMapper = Mappers.getMapper(ProductMapper.class);
    @Spy
    private UniqueFieldService uniqueFieldService = new UniqueFieldServiceImpl();
    @Spy
    private AttachedFileMapper attachedFileMapper = Mappers.getMapper(AttachedFileMapper.class);

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductDto productDto;

    @BeforeEach
    public void setup() {
        ProductCategory category = new ProductCategory(1L, "testCategory", null);
        product = Product.builder().id(1L).name("teatName").category(category).price(101.01d).build();
        productDto = productMapper.mapToDto(product);
    }

    @Test
    void findById_ShouldReturnProductDto_WhenIdExists() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        ProductDto productDtoActual = productService.findById(product.getId());

        assertEquals(productDto, productDtoActual, "ProductDto: expected and actual is not equal.");
    }

    @Test
    void findById_ShouldThrowException_WhenIdNotFound() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productService.findById(1L));
    }

    @ParameterizedTest
    @MethodSource("getCorrectProducts")
    void create_ShouldReturnProductDto_WhenDataIsCorrect(Product product) {
        ProductDto productDto = productMapper.mapToDto(product);

        when(productCategoryService.findById(anyLong())).thenReturn(productDto.getCategory());
        if (Objects.nonNull(productDto.getAge())) {
            when(ageService.findById(anyLong())).thenReturn(productDto.getAge());
        }
        if (Objects.nonNull(productDto.getBrand())) {
            when(brandService.findById(anyLong())).thenReturn(productDto.getBrand());
        }
        if (Objects.nonNull(productDto.getColor())) {
            when(colorService.findById(anyLong())).thenReturn(productDto.getColor());
        }
        if (Objects.nonNull(productDto.getMaterial())) {
            when(materialService.findById(anyLong())).thenReturn(productDto.getMaterial());
        }
        if (Objects.nonNull(productDto.getPrescription())) {
            when(prescriptionService.findById(anyLong())).thenReturn(productDto.getPrescription());
        }
        if (Objects.nonNull(productDto.getProductSize())) {
            when(productSizeService.findById(anyLong())).thenReturn(productDto.getProductSize());
        }
        if (Objects.nonNull(productDto.getWeight())) {
            when(weightService.findById(anyLong())).thenReturn(productDto.getWeight());
        }

        when(productRepository.count(any(Specification.class))).thenReturn(0L);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductDto actualProductDto = productService.create(productDto);

        assertNotNull(actualProductDto);
        assertNotNull(actualProductDto.getId());
        assertNotNull(actualProductDto.getCreatedAt());
        assertEquals(productDto.getName(), actualProductDto.getName(), "ProductDto name: expected and actual is not equal.");
        assertEquals(productDto, actualProductDto, "ProductDto: expected and actual is not equal.");
        assertNull(actualProductDto.getMainImage(), "ProductDto main image: expected value is not null.");
    }

    @ParameterizedTest
    @MethodSource("getProductsToCheckInnerEntities")
    void create_ShouldThrowException_WhenDependentEntityNotFoundById(Product product) {
        ProductDto productDto = productMapper.mapToDto(product);

        when(productRepository.count(any(Specification.class))).thenReturn(0L);

        if (productDto.getCategory().getId() < 10L) {
            doThrow(EntityNotFoundException.class).when(productCategoryService).findById(anyLong());
        } else {
            when(productCategoryService.findById(anyLong())).thenReturn(productDto.getCategory());
        }
        if (Objects.nonNull(productDto.getAge())) {
            doThrow(EntityNotFoundException.class).when(ageService).findById(anyLong());
        }
        if (Objects.nonNull(productDto.getBrand())) {
            doThrow(EntityNotFoundException.class).when(brandService).findById(anyLong());
        }
        if (Objects.nonNull(productDto.getColor())) {
            doThrow(EntityNotFoundException.class).when(colorService).findById(anyLong());
        }
        if (Objects.nonNull(productDto.getMaterial())) {
            doThrow(EntityNotFoundException.class).when(materialService).findById(anyLong());
        }
        if (Objects.nonNull(productDto.getPrescription())) {
            doThrow(EntityNotFoundException.class).when(prescriptionService).findById(anyLong());
        }
        if (Objects.nonNull(productDto.getProductSize())) {
            doThrow(EntityNotFoundException.class).when(productSizeService).findById(anyLong());
        }
        if (Objects.nonNull(productDto.getWeight())) {
            doThrow(EntityNotFoundException.class).when(weightService).findById(anyLong());
        }

        assertThrows(EntityNotFoundException.class, () -> productService.create(productDto));

        verify(productRepository, never()).save(any(Product.class));
    }

    @ParameterizedTest
    @MethodSource("getProductsWithInvalidPrices")
    void create_ShouldThrowException_WhenPriceIsInvalid(Product product) {
        ProductDto productDto = productMapper.mapToDto(product);

        when(productRepository.count(any(Specification.class))).thenReturn(0L);

        assertThrows(PriceConflictException.class, () -> productService.create(productDto));

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void create_ShouldThrowException_WhenNameIsNotUnique() {
        when(productRepository.count(any(Specification.class))).thenReturn(1L);

        assertThrows(EntityDuplicateException.class, () -> productService.create(productDto));

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void update_DataIsCorrect() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(productCategoryService.findById(anyLong())).thenReturn(productDto.getCategory());
        if (Objects.nonNull(productDto.getAge())) {
            when(ageService.findById(anyLong())).thenReturn(productDto.getAge());
        }
        if (Objects.nonNull(productDto.getBrand())) {
            when(brandService.findById(anyLong())).thenReturn(productDto.getBrand());
        }
        if (Objects.nonNull(productDto.getColor())) {
            when(colorService.findById(anyLong())).thenReturn(productDto.getColor());
        }
        if (Objects.nonNull(productDto.getMaterial())) {
            when(materialService.findById(anyLong())).thenReturn(productDto.getMaterial());
        }
        if (Objects.nonNull(productDto.getPrescription())) {
            when(prescriptionService.findById(anyLong())).thenReturn(productDto.getPrescription());
        }
        if (Objects.nonNull(productDto.getProductSize())) {
            when(productSizeService.findById(anyLong())).thenReturn(productDto.getProductSize());
        }
        if (Objects.nonNull(productDto.getWeight())) {
            when(weightService.findById(anyLong())).thenReturn(productDto.getWeight());
        }

        productService.update(productDto.getId(), productDto);

        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void update_ShouldThrowException_WhenProductIdNotFound() {
        when(productRepository.findById(productDto.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productService.update(productDto.getId(), productDto));

        verify(productRepository, never()).save(any(Product.class));
    }

    @ParameterizedTest
    @MethodSource("getProductsToCheckInnerEntities")
    void update_ShouldThrowException_WhenDependentEntityNotFoundById(Product product) {
        ProductDto productDto = productMapper.mapToDto(product);

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        if (productDto.getCategory().getId() < 10L) {
            doThrow(EntityNotFoundException.class).when(productCategoryService).findById(anyLong());
        } else {
            when(productCategoryService.findById(anyLong())).thenReturn(productDto.getCategory());
        }
        if (Objects.nonNull(productDto.getAge())) {
            doThrow(EntityNotFoundException.class).when(ageService).findById(anyLong());
        }
        if (Objects.nonNull(productDto.getBrand())) {
            doThrow(EntityNotFoundException.class).when(brandService).findById(anyLong());
        }
        if (Objects.nonNull(productDto.getColor())) {
            doThrow(EntityNotFoundException.class).when(colorService).findById(anyLong());
        }
        if (Objects.nonNull(productDto.getMaterial())) {
            doThrow(EntityNotFoundException.class).when(materialService).findById(anyLong());
        }
        if (Objects.nonNull(productDto.getPrescription())) {
            doThrow(EntityNotFoundException.class).when(prescriptionService).findById(anyLong());
        }
        if (Objects.nonNull(productDto.getProductSize())) {
            doThrow(EntityNotFoundException.class).when(productSizeService).findById(anyLong());
        }
        if (Objects.nonNull(productDto.getWeight())) {
            doThrow(EntityNotFoundException.class).when(weightService).findById(anyLong());
        }

        assertThrows(EntityNotFoundException.class, () -> productService.update(productDto.getId(), productDto));

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void update_ShouldThrowException_WhenNameIsNotUnique() {
        Product existingProduct = Product.builder().id(1L).name("oldName").category(product.getCategory()).price(1.0).build();
        Product productToUpdate = Product.builder().id(1L).name("newName").category(product.getCategory()).price(1.0).build();
        ProductDto productDtoToUpdate = productMapper.mapToDto(productToUpdate);

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(existingProduct));
        when(productRepository.count(any(Specification.class))).thenReturn(1L);

        assertThrows(EntityDuplicateException.class, () -> productService.update(productDtoToUpdate.getId(), productDtoToUpdate));

        verify(productRepository, never()).save(any(Product.class));
    }

    @ParameterizedTest
    @MethodSource("getImages")
    void update_ShouldThrowException_WhenMainImageNotFound(AttachedFile oldImage) {
        AttachedFile newImage = new AttachedFile(1L, "newPath", "newName", 1L, "Product");
        Product existingProduct = Product.builder().id(1L).name("oldName").mainImage(oldImage).category(product.getCategory()).price(1.0).build();
        Product productToUpdate = Product.builder().id(1L).name("newName").mainImage(newImage).category(product.getCategory()).price(1.0).build();
        ProductDto productDtoToUpdate = productMapper.mapToDto(productToUpdate);

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(existingProduct));
        when(productRepository.count(any(Specification.class))).thenReturn(0L);

        if (!newImage.equals(oldImage)) {
            when(attachedImageService.findByIdAndOwner(newImage.getId(), productMapper.mapToDto(existingProduct)))
                    .thenThrow(EntityNotFoundException.class);
        }

        assertThrows(EntityNotFoundException.class, () -> productService.update(productDtoToUpdate.getId(), productDtoToUpdate));

        verify(productRepository, never()).save(any(Product.class));
    }

    private static Stream<Product> getCorrectProducts() {
        var categoryParent = new ProductCategory(1L, "category", null);
        var categoryChild = new ProductCategory(11L, "category", categoryParent);
        var brand = new Brand(1L, "brand");
        var color = new Color(1L, "color");
        var material = new Material(1L, "material");
        var age = new Age(1L, "age");
        var weight = new Weight(1L, "weight");
        var size = new ProductSize(1L, "size");
        var prescription = new Prescription(1L, "prescription");
        LocalDateTime createdAt = LocalDateTime.now();

        var p1 = Product.builder().id(1L).name("test1").price(1d).priceWithDiscount(0d).category(categoryParent).brand(brand).color(color).material(material).age(age).weight(weight).productSize(size).prescription(prescription).newArrival(true).notAvailable(true).createdAt(createdAt).build();
        var p2 = Product.builder().id(2L).name("test2").price(10d).priceWithDiscount(1d).category(categoryChild).createdAt(createdAt).build();
        var p3 = Product.builder().id(3L).name("test3").price(10.1).category(categoryParent).brand(brand).color(color).material(material).createdAt(createdAt).build();
        var p4 = Product.builder().id(4L).name("test4").price(10.11).category(categoryParent).age(age).weight(weight).productSize(size).prescription(prescription).createdAt(createdAt).build();
        var p5 = Product.builder().id(5L).name("test5").price(10000d).category(categoryParent).color(color).age(age).productSize(size).description("").createdAt(createdAt).build();
        var p6 = Product.builder().id(6L).name("test6").price(1d).category(categoryParent).color(color).material(material).age(age).weight(weight).contraindications("//").createdAt(createdAt).build();

        return new ArrayList<Product>() {{ add(p1); add(p2); add(p3); add(p4); add(p5); add(p6); }}.stream();
    }

    private static Stream<Product> getProductsToCheckInnerEntities() {
        var categoryParent = new ProductCategory(11L, "category", null);
        var categoryChild = new ProductCategory(1L, "category", null);

        var p1 = Product.builder().id(1L).name("test1").price(1d).category(categoryChild).build();
        var p2 = Product.builder().id(2L).name("test2").price(10d).category(categoryParent).brand(new Brand(1L, "brand")).build();
        var p3 = Product.builder().id(3L).name("test3").price(10.1).category(categoryParent).color(new Color(1L, "color")).build();
        var p4 = Product.builder().id(4L).name("test4").price(10.11).category(categoryParent).material(new Material(1L, "material")).build();
        var p5 = Product.builder().id(5L).name("test5").price(10000d).category(categoryParent).age(new Age(1L, "age")).build();
        var p6 = Product.builder().id(6L).name("test6").price(1d).category(categoryParent).weight(new Weight(1L, "weight")).build();
        var p7 = Product.builder().id(7L).name("test7").price(1d).category(categoryParent).productSize(new ProductSize(1L, "size")).build();
        var p8 = Product.builder().id(8L).name("test8").price(1d).category(categoryParent).prescription(new Prescription(1L, "prescription")).build();

        return new ArrayList<Product>() {{ add(p1); add(p2); add(p3); add(p4); add(p5); add(p6); add(p7); add(p8); }}.stream();
    }

    private static Stream<Product> getProductsWithInvalidPrices() {
        var category = new ProductCategory(1L, "category", null);

        var p1 = Product.builder().id(1L).name("test1").price(0d).priceWithDiscount(null).category(category).build();
        var p2 = Product.builder().id(2L).name("test2").price(0d).priceWithDiscount(0d).category(category).build();
        var p3 = Product.builder().id(3L).name("test3").price(0d).priceWithDiscount(1d).category(category).build();
        var p4 = Product.builder().id(4L).name("test4").price(1d).priceWithDiscount(2d).category(category).build();
        var p5 = Product.builder().id(5L).name("test5").price(1d).priceWithDiscount(1d).category(category).build();
        var p6 = Product.builder().id(6L).name("test6").price(0.004).priceWithDiscount(0.00d).category(category).build();
        var p7 = Product.builder().id(7L).name("test7").price(0.01d).priceWithDiscount(0.02d).category(category).build();

        return new ArrayList<Product>() {{ add(p1); add(p2); add(p3); add(p4); add(p5); add(p6); add(p7); }}.stream();
    }

    private static Stream<AttachedFile> getImages() {
        AttachedFile image1 = new AttachedFile(1L, "oldPath", "oldName", 1L, "owner");
        AttachedFile image2 = new AttachedFile(1L, "newPath", "newName", 1L, "Product");

        return new ArrayList<AttachedFile>() {{ add(null); add(image1); add(image2); }}.stream();
    }

    @Test
    void deleteById_WhenDataIsCorrect() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.deleteById(1L);

        verify(productRepository, times(1)).deleteById(1L);
        verify(attachedImageService, times(1)).deleteAllByOwner(productDto);
    }

    @Test
    void deleteById_ShouldThrowException_WhenIdNotFound() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productService.deleteById(anyLong()));

        verify(productRepository, never()).deleteById(anyLong());
        verify(attachedImageService, never()).deleteAllByOwner(any(AttachedImageOwner.class));
    }

    /////////////////////
    // ADD INNER CLASS //
    /////////////////////

    @Test
    void findAllImagesByOwnerId_ShouldReturnAttachedFileDtoSet() {
        Set<AttachedFileDto> expectedResult = new HashSet<>() {{
            add(new AttachedFileDto(1L, "path1", "name1"));
            add(new AttachedFileDto(2L, "path2", "name2"));
            add(new AttachedFileDto(3L, "path3", "name3"));
        }};

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(attachedImageService.findAllByOwner(productDto)).thenReturn(expectedResult);

        Set<AttachedFileDto> actualResult = productService.findAllImagesByOwnerId(productDto.getId());

        assertEquals(expectedResult.size(), actualResult.size(), "AttachedFileDto set size: expected and actual is not equal.");
        assertIterableEquals(expectedResult, actualResult, "AttachedFileDto set: expected and actual don't have the same elements in the same order.");
    }

    @Test
    void findAllImagesByOwnerId_ShouldThrowException_WhenIdNotFound() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productService.findAllImagesByOwnerId(anyLong()));
    }

    @Test
    void findImageByIdAndOwnerId_ShouldReturnAttachedFileDto() {
        AttachedFileDto expectedImageDto = new AttachedFileDto(1L, "testPath", "testName");

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(attachedImageService.findByIdAndOwner(anyLong(), any(AttachedImageOwner.class))).thenReturn(expectedImageDto);

        AttachedFileDto actualImageDto = productService.findImageByIdAndOwnerId(expectedImageDto.getId(), product.getId());

        assertEquals(expectedImageDto, actualImageDto, "ImageDto: expected and actual is not equal.");
    }

    @Test
    void findImageByIdAndOwnerId_ShouldThrowException_WhenOwnerNotFoundById() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productService.findImageByIdAndOwnerId(1L, 1L));

        verify(attachedImageService, never()).findByIdAndOwner(anyLong(), any(AttachedImageOwner.class));
    }

    @Test
    void uploadImagesByOwnerId_ShouldReturnAttachedFileDtoSet_WhenDataIsCorrect() {
        List<MultipartFile> incomingImages = new ArrayList<>(){{
            add(new MockMultipartFile("testName1", "name1.png", "", new byte[]{0}));
            add(new MockMultipartFile("testName2", "name2.png", "", new byte[]{0}));
        }};
        Set<AttachedFileDto> expectedResult = new HashSet<>() {{
            add(new AttachedFileDto(1L, "path1", "name1withAdditionalInfo.png"));
            add(new AttachedFileDto(2L, "path2", "name2withAdditionalInfo.jpg"));
        }};

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(attachedImageService.uploadFilesByOwner(productDto, incomingImages)).thenReturn(expectedResult);

        Set<AttachedFileDto> actualImageDtoSet = productService.uploadImagesByOwnerId(product.getId(), incomingImages);

        assertNotNull(actualImageDtoSet, "AttachedFileDto set: ");
        assertEquals(expectedResult.size(), actualImageDtoSet.size(), "AttachedFileDto set size: expected and actual is not equal.");
        assertIterableEquals(expectedResult, actualImageDtoSet, "AttachedFileDto set: expected and actual don't have the same elements in the same order.");
    }

    @Test
    void uploadImageByOwnerId_ShouldThrowException_WhenOwnerNotFoundById() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productService.uploadImagesByOwnerId(1L, new ArrayList<>()));

        verify(attachedImageService, never()).uploadFilesByOwner(any(AttachedImageOwner.class), any(List.class));
    }

    @Test
    void deleteAllImagesByOwnerId_WhenDataIsCorrect() {
        product.setMainImage(new AttachedFile(1L, "path1", "name1withAdditionalInfo.png", 1L, "Product"));

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        doNothing().when(attachedImageService).deleteAllByOwner(productDto);

        // act
        productService.deleteAllImagesByOwnerId(product.getId());

        assertNull(product.getMainImage(), "Product main image: expected value is not null.");

        verify(attachedImageService, times(1)).deleteAllByOwner(productDto);
    }

    @Test
    void deleteAllImagesByOwnerId_ShouldThrowException_WhenOwnerNotFoundById() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productService.deleteAllImagesByOwnerId(1L));

        verify(attachedImageService, never()).deleteAllByOwner(any(AttachedImageOwner.class));
    }

    @Test
    void deleteImageByIdAndOwnerId_WhenDeletedImageIsMain() {
        AttachedFile image = new AttachedFile(1L, "path1", "name.png", 1L, "Product");
        product.setMainImage(image);

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        doNothing().when(attachedImageService).deleteByIdAndOwner(image.getId(), productDto);

        productService.deleteImageByIdAndOwnerId(image.getId(), productDto.getId());

        assertNull(product.getMainImage(), "Product main image: expected value is not null.");

        verify(attachedImageService, times(1)).deleteByIdAndOwner(image.getId(), productDto);
    }

    @Test
    void deleteImageByIdAndOwnerId_WhenDeletedImageIsNotMain() {
        AttachedFile imageToBeDeleted = new AttachedFile(1L, "path1", "name.png", 1L, "Product");
        AttachedFile mainImage = new AttachedFile(2L, "path2", "name.png", 1L, "Product");
        product.setMainImage(mainImage);

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        doNothing().when(attachedImageService).deleteByIdAndOwner(imageToBeDeleted.getId(), productDto);

        // act
        productService.deleteImageByIdAndOwnerId(imageToBeDeleted.getId(), productDto.getId());

        assertEquals(mainImage, product.getMainImage(), "Product main image: expected and actual is not equal.");

        verify(attachedImageService, times(1)).deleteByIdAndOwner(imageToBeDeleted.getId(), productDto);
    }

    @Test
    void deleteImageByIdAndOwnerId_ShouldThrowException_WhenOwnerNotFoundById() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productService.deleteImageByIdAndOwnerId(1L, 1L));

        verify(attachedImageService, never()).deleteByIdAndOwner(anyLong(), any(AttachedImageOwner.class));
    }
}
