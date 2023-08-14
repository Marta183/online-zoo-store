package kms.onlinezoostore.controllers;

import jakarta.validation.Valid;
import kms.onlinezoostore.dto.MaterialDto;
import kms.onlinezoostore.services.MaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = MaterialController.REST_URL)
public class MaterialController {
    static final String REST_URL = "/api/v1/materials";
    private final MaterialService materialService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<MaterialDto> findAll() {
        return materialService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MaterialDto findById(@PathVariable Long id) {
        return materialService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MaterialDto create(@RequestBody @Valid MaterialDto materialDto) {
        return materialService.create(materialDto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable Long id, @RequestBody @Valid MaterialDto materialDto) {
        materialService.update(id, materialDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        materialService.deleteById(id);
    }
}
