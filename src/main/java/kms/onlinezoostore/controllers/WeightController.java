package kms.onlinezoostore.controllers;

import jakarta.validation.Valid;
import kms.onlinezoostore.dto.WeightDto;
import kms.onlinezoostore.services.WeightService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = WeightController.REST_URL)
public class WeightController {
    static final String REST_URL = "/api/v1/weights";
    private final WeightService weightService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<WeightDto> findAll() {
        return weightService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public WeightDto findById(@PathVariable Long id) {
        return weightService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WeightDto create(@RequestBody @Valid WeightDto weightDto) {
        return weightService.create(weightDto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable Long id, @RequestBody @Valid WeightDto weightDto) {
        weightService.update(id, weightDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        weightService.deleteById(id);
    }
}
