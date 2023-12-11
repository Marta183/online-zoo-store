package kms.onlinezoostore.controllers.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kms.onlinezoostore.dto.ConstantDto;
import kms.onlinezoostore.entities.enums.ConstantKeys;
import kms.onlinezoostore.services.ConstantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

@RestController
@Tag(name = "Constants")
@RequiredArgsConstructor
@RequestMapping(value = ConstantController.REST_URL)
public class ConstantController {
    static final String REST_URL = "/api/v1/constants";

    private final ConstantService constantService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all constants", description = "Retrieve a list of all constants")
    public List<ConstantDto> findAll() {
        return constantService.findAll();
    }

    @GetMapping("/{key}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get constant by key", description = "Retrieve the value of a constant by its key")
    public ConstantDto findValueByKey(@PathVariable ConstantKeys key) {
        return constantService.findByKey(key);
    }

    @PutMapping("/{key}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update constant value", description = "Update the value of a constant by its key")
    public Object updateValue(@PathVariable ConstantKeys key, @RequestParam(name = "value") Object updatedValue) {
        return constantService.updateValue(key, updatedValue);
    }

    @DeleteMapping("/{key}/image")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete image by constant key", description = "Delete image associated with a constant by its key")
    public void deleteImagesByKey(@PathVariable ConstantKeys key) {
        constantService.deleteImage(key);
    }
}
