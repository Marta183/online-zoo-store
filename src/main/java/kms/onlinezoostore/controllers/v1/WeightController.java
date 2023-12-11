package kms.onlinezoostore.controllers.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kms.onlinezoostore.dto.WeightDto;
import kms.onlinezoostore.services.WeightService;
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
@Tag(name = "Weights")
@RequiredArgsConstructor
@RequestMapping(value = WeightController.REST_URL)
public class WeightController {
    static final String REST_URL = "/api/v1/weights";
    private final WeightService weightService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all weights", description = "Get a list of all weights")
    public List<WeightDto> findAll() {
        return weightService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get weight by ID", description = "Get weight details by ID")
    public WeightDto findById(@PathVariable Long id) {
        return weightService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new weight", description = "Create a new weight with the provided details")
    public WeightDto create(@RequestBody @Valid WeightDto weightDto) {
        return weightService.create(weightDto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update weight by ID", description = "Update an existing weight by ID and details")
    public void update(@PathVariable Long id, @RequestBody @Valid WeightDto weightDto) {
        weightService.update(id, weightDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete weight by ID", description = "Delete an existing weight by ID")
    public void deleteById(@PathVariable Long id) {
        weightService.deleteById(id);
    }
}
