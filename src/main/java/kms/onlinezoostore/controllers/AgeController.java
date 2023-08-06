package kms.onlinezoostore.controllers;

import jakarta.validation.Valid;
import kms.onlinezoostore.dto.AgeDto;
import kms.onlinezoostore.services.AgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = AgeController.REST_URL)
//@Api(description = "Controller for Ages")
public class AgeController {
    static final String REST_URL = "/api/v1/Ages";
    private final AgeService ageService;

    @Autowired
    public AgeController(AgeService ageService) {
        this.ageService = ageService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<AgeDto> findAll() {
        return ageService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AgeDto findById(@PathVariable Long id) {
        return ageService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AgeDto create(@RequestBody @Valid AgeDto ageDto) {
        return ageService.create(ageDto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable Long id, @RequestBody @Valid AgeDto ageDto) {
        ageService.update(id, ageDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        ageService.deleteById(id);
    }
}
