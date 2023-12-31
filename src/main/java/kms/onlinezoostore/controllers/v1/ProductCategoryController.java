package kms.onlinezoostore.controllers.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kms.onlinezoostore.dto.AttachedFileDto;
import kms.onlinezoostore.dto.ProductCategoryDto;
import kms.onlinezoostore.services.ProductCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

@RestController
@Tag(name = "Product categories")
@RequiredArgsConstructor
@RequestMapping(value = ProductCategoryController.REST_URL)
public class ProductCategoryController {
    static final String REST_URL = "/api/v1/product-categories";

    private final ProductCategoryService categoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all categories",
               description = "Retrieve a list of all categories. Optionally, filter by name")
    public List<ProductCategoryDto> findAll(
            @RequestParam(name = "nameLike", required = false) String nameLike) {
        if (nameLike != null && !nameLike.isBlank()) {
            return categoryService.findAllByNameLike(nameLike);
        }
        return categoryService.findAll();
    }

    @GetMapping("/main")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all main categories",
               description = "Retrieve a list of all main categories")
    public List<ProductCategoryDto> findAllMain() {
        return categoryService.findAllMainCategories();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get category by ID",
               description = "Retrieve a category by ID")
    public ProductCategoryDto findById(@PathVariable Long id) {
        return categoryService.findById(id);
    }

    @GetMapping("/{id}/inner-categories")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get inner categories by parent ID",
               description = "Retrieve a list of inner categories by parent ID")
    public List<ProductCategoryDto> findAllByParentId(@PathVariable Long id) {
        return categoryService.findAllByParentId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new category",
               description = "Create a new category with the provided details")
    public ProductCategoryDto create(@RequestBody @Valid ProductCategoryDto productCategoryDto) {
        return categoryService.create(productCategoryDto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update category by ID",
               description = "Update an existing category by ID and details")
    public void update(@PathVariable Long id, @RequestBody @Valid ProductCategoryDto productCategoryDto) {
        categoryService.update(id, productCategoryDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete category by ID",
               description = "Delete an existing category by ID")
    public void deleteById(@PathVariable Long id) {
        categoryService.deleteById(id);
    }


    //// IMAGES ////

    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Upload category image",
               description = "Upload a new image for the category providing category ID and image file")
    public AttachedFileDto uploadImage(@PathVariable Long id, @RequestParam("image") MultipartFile image) {
        return categoryService.uploadImageByOwnerId(id, image);
    }

    @DeleteMapping("/{id}/image")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete category image",
               description = "Delete category image by category ID")
    public void deleteAllImages(@PathVariable Long id) {
        categoryService.deleteImageByOwnerId(id);
    }
}
