package kms.onlinezoostore.controllers.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kms.onlinezoostore.dto.AttachedFileDto;
import kms.onlinezoostore.dto.BrandDto;
import kms.onlinezoostore.services.BrandService;
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
@Tag(name = "Brands")
@RequiredArgsConstructor
@RequestMapping(value = BrandController.REST_URL)
public class BrandController {
    static final String REST_URL = "/api/v1/brands";

    private final BrandService brandService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all brands", description = "Get a list of all brands")
    public List<BrandDto> findAll() {
        return brandService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get brand by ID", description = "Get brand details by ID")
    public BrandDto findById(@PathVariable Long id) {
        return brandService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new brand", description = "Create a new brand with the provided details")
    public BrandDto create(@RequestBody @Valid BrandDto brandDto) {
        return brandService.create(brandDto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update brand by ID", description = "Update an existing brand by ID and details")
    public void update(@PathVariable Long id,
                       @RequestBody @Valid BrandDto brandDto) {
        brandService.update(id, brandDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete brand by ID", description = "Delete an existing brand by ID")
    public void deleteById(@PathVariable Long id) {
        brandService.deleteById(id);
    }


    //// IMAGES ////

    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Upload brand image",
               description = "Upload a new image for the brand by providing brand ID and image file")
    public AttachedFileDto uploadImage(@PathVariable Long id,
                                       @RequestParam("image") MultipartFile image) {
        return brandService.uploadImageByOwnerId(id, image);
    }

    @DeleteMapping("/{id}/image")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete brand image",
               description = "Delete image associated with the brand by providing brand ID")
    public void deleteAllImages(@PathVariable Long id) {
        brandService.deleteImageByOwnerId(id);
    }
}
