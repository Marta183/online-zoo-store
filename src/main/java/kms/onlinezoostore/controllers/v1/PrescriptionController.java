package kms.onlinezoostore.controllers.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kms.onlinezoostore.dto.PrescriptionDto;
import kms.onlinezoostore.services.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "Prescriptions")
@RequiredArgsConstructor
@RequestMapping(value = PrescriptionController.REST_URL)
public class PrescriptionController {
    static final String REST_URL = "/api/v1/prescriptions";
    private final PrescriptionService prescriptionService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all prescriptions", description = "Get a list of all prescriptions")
    public List<PrescriptionDto> findAll() {
        return prescriptionService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get prescription by ID", description = "Get prescription details by ID")
    public PrescriptionDto findById(@PathVariable Long id) {
        return prescriptionService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new prescription", description = "Create a new prescription with the provided details")
    public PrescriptionDto create(@RequestBody @Valid PrescriptionDto prescriptionDto) {
        return prescriptionService.create(prescriptionDto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update prescription by ID", description = "Update an existing prescription by ID and details")
    public void update(@PathVariable Long id, @RequestBody @Valid PrescriptionDto prescriptionDto) {
        prescriptionService.update(id, prescriptionDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete prescription by ID", description = "Delete an existing prescription by ID")
    public void deleteById(@PathVariable Long id) {
        prescriptionService.deleteById(id);
    }
}
