package kms.onlinezoostore.controllers;

import jakarta.validation.Valid;
import kms.onlinezoostore.dto.ColorDto;
import kms.onlinezoostore.services.ColorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = ColorController.REST_URL)
public class ColorController {
    static final String REST_URL = "/api/v1/colors";
    private final ColorService colorService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ColorDto> findAll() {
        return colorService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ColorDto findById(@PathVariable Long id) {
        return colorService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ColorDto create(@RequestBody @Valid ColorDto colorDto) {
        return colorService.create(colorDto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable Long id, @RequestBody @Valid ColorDto colorDto) {
        colorService.update(id, colorDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        colorService.deleteById(id);
    }
}
