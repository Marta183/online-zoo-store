package kms.onlinezoostore.controllers.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kms.onlinezoostore.config.springdoc.PageableAsQueryParam;
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
@Tag(name = "Products")
@RequiredArgsConstructor
@RequestMapping(value = ProductController.REST_URL)
public class ProductController {
    static final String REST_URL = "/api/v1/products";
    private final ProductService productService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Find product by ID", description = "Get a product details by its unique ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved product details")
    @ApiResponse(responseCode = "404", description = "Product not found")
    public ProductDto findProductById(@PathVariable Long id) {
        return productService.findById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PageableAsQueryParam
    @Operation(summary = "Find product page", description = "Retrieve a paginated list of products based on specified filtration parameters")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved paginated list of products")
    @ApiResponse(responseCode = "400", description = "Bad request")
    public Page<ProductDto> findPageByCriteria(
                @Parameter(description = "Allowed filtration parameters: " +
                    "nameLike, nameStartsWith, minPrice, maxPrice, onSale, newArrival, notAvailable" +
                    "categoryId, brandId, colorId, materialId, weightId, sizeId, ageId, prescriptionId")
                @RequestParam MultiValueMap<String, String> params,
                @Parameter(hidden = true) Pageable pageable) {

        params.remove("pageNumber");
        params.remove("pageSize");
        params.remove("sort");

        if (params.isEmpty()) {
            return productService.findPage(pageable);
        }
        return productService.findPageByMultipleCriteria(params, pageable);
    }

    @GetMapping("max-price")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Find max product price")
    public Double findMaxPrice() {
        return productService.findMaxProductPrice();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new product", description = "Create a new product with the provided details")
    @ApiResponse(responseCode = "201", description = "Successfully created product")
    @ApiResponse(responseCode = "400", description = "Bad request")
    @ApiResponse(responseCode = "404", description = "Product characteristic not found")
    @ApiResponse(responseCode = "409", description = "Product duplicate or price conflict")
    public ProductDto createProduct(@RequestBody @Valid ProductDto productDto) {
        return productService.create(productDto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update an existing product", description = "Update an existing product with the provided details")
    @ApiResponse(responseCode = "200", description = "Successfully updated product")
    @ApiResponse(responseCode = "400", description = "Bad request")
    @ApiResponse(responseCode = "404", description = "Product or characteristic or main image not found")
    @ApiResponse(responseCode = "409", description = "Product duplicate or price conflict")
    public void updateProduct(@PathVariable Long id, @RequestBody @Valid ProductDto productDto) {
        productService.update(id, productDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete product by ID", description = "Delete a product by its unique ID")
    @ApiResponse(responseCode = "204", description = "Successfully deleted product")
    @ApiResponse(responseCode = "404", description = "Product or image not found")
    @ApiResponse(responseCode = "500", description = "Remote image service conflict")
    public void deleteProductById(@PathVariable Long id) {
        productService.deleteById(id);
    }


    //// IMAGES ////


    @GetMapping("/{productId}/images/{imageId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Find product image by product ID and image ID",
               description = "Get a specific image associated with a product by product ID and image ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved product image")
    @ApiResponse(responseCode = "404", description = "Product or image not found")
    public AttachedFileDto findImage(@PathVariable Long productId, @PathVariable Long imageId) {
        return productService.findImageByIdAndOwnerId(productId, imageId);
    }

    @PostMapping(value = "/{productId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Upload images for a product",
               description = "Upload multiple images for a product by product ID")
    @ApiResponse(responseCode = "201", description = "Successfully uploaded product images")
    @ApiResponse(responseCode = "400", description = "Bad request")
    @ApiResponse(responseCode = "404", description = "Product not found")
    @ApiResponse(responseCode = "500", description = "Remote image service conflict")
    @ApiResponse(responseCode = "503", description = "Images service unavailable")
    public Set<AttachedFileDto> uploadImages(@PathVariable Long productId,
                                             @RequestParam("images") List<MultipartFile> images) {
        return productService.uploadImagesByOwnerId(productId, images);
    }

    @DeleteMapping("/{productId}/images")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete all images for a product",
               description = "Delete all images associated with a product by product ID")
    @ApiResponse(responseCode = "204", description = "Successfully deleted all product images")
    @ApiResponse(responseCode = "404", description = "Product not found")
    @ApiResponse(responseCode = "500", description = "Remote image service conflict")
    public void deleteAllImages(@PathVariable Long productId) {
        productService.deleteAllImagesByOwnerId(productId);
    }

    @DeleteMapping("/{productId}/images/{imageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a specific image for a product",
               description = "Delete a specific image associated with a product by product ID and image ID")
    @ApiResponse(responseCode = "204", description = "Successfully deleted product image")
    @ApiResponse(responseCode = "404", description = "Product or image not found")
    @ApiResponse(responseCode = "500", description = "Remote image service conflict")
    public void deleteImage(@PathVariable Long productId, @PathVariable Long imageId) {
        productService.deleteImageByIdAndOwnerId(productId, imageId);
    }
}
