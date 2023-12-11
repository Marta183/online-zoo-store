package kms.onlinezoostore.controllers.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kms.onlinezoostore.dto.MaterialDto;
import kms.onlinezoostore.services.MaterialService;
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

import java.util.List;

@RestController
@Tag(name = "Materials")
@RequiredArgsConstructor
@RequestMapping(value = MaterialController.REST_URL)
public class MaterialController {
    static final String REST_URL = "/api/v1/materials";
    private final MaterialService materialService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all materials", description = "Get a list of all materials")
    public List<MaterialDto> findAll() {
        return materialService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get material by ID", description = "Get material details by ID")
    public MaterialDto findById(@PathVariable Long id) {
        return materialService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new material", description = "Create a new material with the provided details")
    public MaterialDto create(@RequestBody @Valid MaterialDto materialDto) {
        return materialService.create(materialDto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update material by ID", description = "Update an existing material by ID and details")
    public void update(@PathVariable Long id, @RequestBody @Valid MaterialDto materialDto) {
        materialService.update(id, materialDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete material by ID", description = "Delete an existing material by ID")
    public void deleteById(@PathVariable Long id) {
        materialService.deleteById(id);
    }
}
