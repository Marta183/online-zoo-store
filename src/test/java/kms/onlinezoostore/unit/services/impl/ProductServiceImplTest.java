package kms.onlinezoostore.unit.services.impl;

import kms.onlinezoostore.dto.ProductDto;
import kms.onlinezoostore.dto.mappers.AttachedFileMapper;
import kms.onlinezoostore.dto.mappers.AttachedFileMapperImpl;
import kms.onlinezoostore.dto.mappers.ProductMapper;
import kms.onlinezoostore.entities.Product;
import kms.onlinezoostore.exceptions.EntityDuplicateException;
import kms.onlinezoostore.exceptions.EntityNotFoundException;
import kms.onlinezoostore.repositories.ProductRepository;
import kms.onlinezoostore.repositories.specifications.ProductSpecifications;
import kms.onlinezoostore.services.files.images.AttachedImageService;
import kms.onlinezoostore.services.impl.ProductServiceImpl;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.MultiValueMap;

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
public class ProductServiceImplTest {
//
//    @Mock
//    private ProductRepository productRepository;
//    @Spy
//    private ProductMapper productMapper = Mappers.getMapper(ProductMapper.class);
//    @Spy
//    private AttachedFileMapper attachedFileMapper = new AttachedFileMapperImpl();
//    @Spy
//    private UniqueFieldService uniqueFieldService = new UniqueFieldServiceImpl();
//    @Mock
//    private AttachedImageService attachedImageService;
//
//    @InjectMocks
//    private ProductServiceImpl productService;
//
//    private Product product;
//    private ProductDto productDto;
//    private List<Product> productList;
//    private List<ProductDto> productDtoList;
//
//    @BeforeEach
//    public void setup(){
//        product = new Product(1L, "test1");
//        productDto = productMapper.mapToDto(product);
//
//        productList = new ArrayList<>();
//        productList.add(product);
//        productList.add(new Product(2L, "test2"));
//        productList.add(new Product(3L, "test3"));
//
//        productDtoList = productList.stream().map((el) -> productMapper.mapToDto(el)).collect(Collectors.toList());
//    }
//
//    @Test
//    void findById_ShouldReturnProductDto_WhenIdExists() {
//        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
//
//        ProductDto productDtoActual = productService.findById(product.getId());
//
//        assertEquals(productDto, productDtoActual, "ProductDto: expected and actual is not equal.");
//    }
//
//    @Test
//    void findById_ShouldThrowException_WhenIdNotFound() {
//        when(productRepository.findById(product.getId())).thenReturn(Optional.empty());
//
//        assertThrows(EntityNotFoundException.class, () -> productService.findById(product.getId()));
//    }
//
//    @Test
//    void findPage_ShouldReturnProductPage_WhenPageableIsCorrect() {
//        Pageable pageable = PageRequest.of(0, 5);
//        pageable.
//        when(productRepository.findAll(any(Pageable.class))).thenReturn();
//
//        Page<ProductDto> page =  productRep.findAll(pageable)
//                .map(productMapper::mapToDto);
//    }
//
//    @Test
//    void testFindPage() {
//        // Подготовка тестовых данных
//        List<Product> products = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            Product product = new Product();
//            product.setId((long) i);
//            // Заполните другие поля продукта при необходимости
//            products.add(product);
//        }
//
//        Pageable pageable = PageRequest.of(0, 5); // Например, первая страница, по 5 элементов на странице
//
//        // Настройка поведения моков
//        when(productRepository.findAll(pageable)).thenReturn(new PageImpl<>(products));
//
//        List<ProductDto> productDtos = new ArrayList<>();
//        for (Product product : products) {
//            // Подготовьте ProductDto для каждого продукта при необходимости
//            ProductDto productDto = new ProductDto();
//            productDtos.add(productDto);
//        }
//
//        when(productMapper.mapToDto(any(Product.class))).thenAnswer(invocation -> {
//            Product product = invocation.getArgument(0);
//            // Вернуть соответствующий ProductDto для переданного продукта
//            return productDtos.get(products.indexOf(product));
//        });
//
//        // Вызов тестируемого метода
//        Page<ProductDto> resultPage = productService.findPage(pageable);
//
//        // Проверка результатов
//        assertEquals(products.size(), resultPage.getContent().size()); // Проверка размера страницы
//        for (int i = 0; i < products.size(); i++) {
//            Product product = products.get(i);
//            ProductDto expectedDto = productDtos.get(i);
//            ProductDto actualDto = resultPage.getContent().get(i);
//
//            assertEquals(expectedDto, actualDto); // Проверка, что ProductDto корректно отображается
//        }
//    }
//
//
//    @Test
//    void findPageByCategoryId(Long categoryId, Pageable pageable) {
//        Page<ProductDto> page =  productRep.findAllByCategoryId(categoryId, pageable)
//                .map(productMapper::mapToDto);
//    }
//
//    @Test
//    void findPageByBrandId(Long brandId, Pageable pageable) {
//        Page<ProductDto> page =  productRep.findAllByBrandId(brandId, pageable)
//                .map(productMapper::mapToDto);
//    }
//
//    @Test
//    void findPageByMultipleCriteria(MultiValueMap<String, String> params, Pageable pageable) {
//        processParamsForCriteriaBuilder(params);
//
//        Page<ProductDto> page = productRep.findAll(ProductSpecifications.build(params), pageable)
//                .map(productMapper::mapToDto);
//    }
//
//
////    @Test
////    void findAll_ShouldReturnProductList() {
////        when(productRepository.findAll()).thenReturn(productList);
////
////        List<ProductDto> actualProductDtoList = productService.findAll();
////
////        assertEquals(productDtoList.size(), actualProductDtoList.size(), "ProductDto list size: expected and actual is not equal.");
////        assertIterableEquals(productDtoList, actualProductDtoList, "ProductDto list: expected and actual don't have the same elements in the same order.");
////    }
////
////    @Test
////    void findAll_ShouldReturnEmptyList() {
////        when(productRepository.findAll()).thenReturn(Collections.emptyList());
////
////        List<ProductDto> actualProductDtoList = productService.findAll();
////
////        assertEquals(0, actualProductDtoList.size(), "Actual productDto list is not empty");
////    }
//
//    @Test
//    void create_ShouldReturnProductDto() {
//        when(productRepository.count(any(Specification.class))).thenReturn(0L);
//        when(productRepository.save(any(Product.class))).thenReturn(product);
//
//        ProductDto actualSavedProductDto = productService.create(productDto);
//
//        assertNotNull(actualSavedProductDto);
//        assertNotNull(actualSavedProductDto.getId());
//        assertEquals(productDto.getName(), actualSavedProductDto.getName(), "ProductDto name: expected and actual is not equal.");
//    }
//
//    @Test
//    void create_ShouldThrowException_WhenNameIsNotUnique() {
//        when(productRepository.count(any(Specification.class))).thenReturn(1L);
//
//        assertThrows(EntityDuplicateException.class, () -> productService.create(productDto));
//
//        verify(productRepository, never()).save(any(Product.class));
//    }
//
//    @Test
//    void update_DataIsCorrect() {
//        when(productRepository.findById(productDto.getId())).thenReturn(Optional.of(product));
//
//        productService.update(productDto.getId(), productDto);
//
//        verify(productRepository, times(1)).save(any(Product.class));
//    }
//
//    @Test
//    void update_ShouldThrowException_WhenIdNotFound() {
//        when(productRepository.findById(productDto.getId())).thenReturn(Optional.empty());
//
//        assertThrows(EntityNotFoundException.class, () -> productService.update(productDto.getId(), productDto));
//
//        verify(productRepository, never()).save(any(Product.class));
//    }
//
//    @Test
//    void update_ShouldThrowException_WhenNameIsNotUnique() {
//        Product productExisting = new Product(1L, "test1");
//        ProductDto productDtoToUpdate = new ProductDto(1L, "test2");
//
//        when(productRepository.count(any(Specification.class))).thenReturn(1L);
//        when(productRepository.findById(anyLong())).thenReturn(Optional.of(productExisting));
//
//        assertThrows(EntityDuplicateException.class, () -> productService.update(productDtoToUpdate.getId(), productDtoToUpdate));
//
//        verify(productRepository, never()).save(any(Product.class));
//    }
//
//    @Test
//    void deleteById_WhenDataIsCorrect() {
//        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
//
//        productService.deleteById(1L);
//
//        verify(productRepository, times(1)).deleteById(1L);
//    }
//
//    @Test
//    void deleteById_ShouldThrowException_WhenIdNotFound() {
//        when(productRepository.findById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(EntityNotFoundException.class, () -> productService.deleteById(1L));
//
//        verify(productRepository, never()).deleteById(anyLong());
//    }
}
