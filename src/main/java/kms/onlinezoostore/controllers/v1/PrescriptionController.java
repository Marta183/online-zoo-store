package kms.onlinezoostore.controllers.v1;

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
@RequiredArgsConstructor
@RequestMapping(value = PrescriptionController.REST_URL)
public class PrescriptionController {
    static final String REST_URL = "/api/v1/prescriptions";
    private final PrescriptionService prescriptionService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<PrescriptionDto> findAll() {
        return prescriptionService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PrescriptionDto findById(@PathVariable Long id) {
        return prescriptionService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PrescriptionDto create(@RequestBody @Valid PrescriptionDto prescriptionDto) {
        return prescriptionService.create(prescriptionDto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable Long id, @RequestBody @Valid PrescriptionDto prescriptionDto) {
        prescriptionService.update(id, prescriptionDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        prescriptionService.deleteById(id);
    }
}
