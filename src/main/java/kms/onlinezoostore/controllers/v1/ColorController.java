package kms.onlinezoostore.controllers.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kms.onlinezoostore.dto.AttachedFileDto;
import kms.onlinezoostore.dto.ColorDto;
import kms.onlinezoostore.services.ColorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Tag(name = "Colors")
@RequiredArgsConstructor
@RequestMapping(value = ColorController.REST_URL)
public class ColorController {
    static final String REST_URL = "/api/v1/colors";
    private final ColorService colorService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all colors", description = "Get a list of all colors")
    public List<ColorDto> findAll() {
        return colorService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get color by ID", description = "Get color details by ID")
    public ColorDto findById(@PathVariable Long id) {
        return colorService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new color", description = "Create a new color with the provided details")
    public ColorDto create(@RequestBody @Valid ColorDto colorDto) {
        return colorService.create(colorDto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update color by ID", description = "Update an existing color by ID and details")
    public void update(@PathVariable Long id, @RequestBody @Valid ColorDto colorDto) {
        colorService.update(id, colorDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete color by ID", description = "Delete an existing color by ID")
    public void deleteById(@PathVariable Long id) {
        colorService.deleteById(id);
    }

    //// IMAGES ////

    @PostMapping(value = "/{id}/image")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Upload color image",
            description = "Upload a new image for the color by providing color ID and image file")
    public AttachedFileDto uploadImage(@PathVariable Long id,
                                       @RequestParam("image") MultipartFile image) {
        return colorService.uploadImageByOwnerId(id, image);
    }

    @DeleteMapping("/{id}/image")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete brand image",
            description = "Delete image associated with the color by providing color ID")
    public void deleteAllImages(@PathVariable Long id) {
        colorService.deleteImageByOwnerId(id);
    }
}
