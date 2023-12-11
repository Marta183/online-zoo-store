package kms.onlinezoostore.controllers.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kms.onlinezoostore.dto.AgeDto;
import kms.onlinezoostore.services.AgeService;
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
@Tag(name = "Ages")
@RequiredArgsConstructor
@RequestMapping(value = AgeController.REST_URL)
public class AgeController {
    static final String REST_URL = "/api/v1/ages";
    private final AgeService ageService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all ages", description = "Get a list of all ages")
    public List<AgeDto> findAll() {
        return ageService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get age by ID", description = "Get age details by ID")
    public AgeDto findById(@PathVariable Long id) {
        return ageService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new age", description = "Create a new age with the provided details")
    public AgeDto create(@RequestBody @Valid AgeDto ageDto) {
        return ageService.create(ageDto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update age by ID", description = "Update an existing age by ID and details")
    public void update(@PathVariable Long id, @RequestBody @Valid AgeDto ageDto) {
        ageService.update(id, ageDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete age by ID", description = "Delete an existing age by ID")
    public void deleteById(@PathVariable Long id) {
        ageService.deleteById(id);
    }
}
