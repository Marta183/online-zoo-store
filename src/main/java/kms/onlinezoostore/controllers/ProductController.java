package kms.onlinezoostore.controllers;

import jakarta.validation.Valid;
import kms.onlinezoostore.dto.AttachedFileDto;
import kms.onlinezoostore.dto.ProductDto;
import kms.onlinezoostore.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = ProductController.REST_URL)
public class ProductController {
    static final String REST_URL = "/api/v1/products";
    private final ProductService productService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProductDto findProductById(@PathVariable Long id) {
        return productService.findById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<ProductDto> findPageByCriteria(@RequestParam MultiValueMap<String, String> params, Pageable pageable) {
        params.remove("pageNumber");
        params.remove("pageSize");
        params.remove("sort");

        if (params.isEmpty()) {
            return productService.findPage(pageable);
        }
        return productService.findPageByMultipleCriteria(params, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDto createProduct(@RequestBody @Valid ProductDto productDto) {
        return productService.create(productDto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateProduct(@PathVariable Long id, @RequestBody @Valid ProductDto productDto) {
        productService.update(id, productDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProductById(@PathVariable Long id) {
        productService.deleteById(id);
    }

//    @GetMapping("/max-price")
//    @ResponseStatus(HttpStatus.OK)
//    public List<Product> findMaxProductPrice(@PathVariable Long categoryId) {
//        return
//    }


    //// IMAGES ////


    @GetMapping("/{productId}/images/{imageId}")
    @ResponseStatus(HttpStatus.OK)
    public AttachedFileDto findImage(@PathVariable Long productId, @PathVariable Long imageId) {
        return productService.findImageByIdAndOwnerId(productId, imageId);
    }

    @PostMapping(value = "/{productId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Set<AttachedFileDto> uploadImages(@PathVariable Long productId,
                                             @RequestParam("images") List<MultipartFile> images) {
        return productService.uploadImagesByOwnerId(productId, images);
    }

    @DeleteMapping("/{productId}/images")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllImages(@PathVariable Long productId) {
        productService.deleteAllImagesByOwnerId(productId);
    }

    @DeleteMapping("/{productId}/images/{imageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteImage(@PathVariable Long productId, @PathVariable Long imageId) {
        productService.deleteImageByIdAndOwnerId(productId, imageId);
    }

}
