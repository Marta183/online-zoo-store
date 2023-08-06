package kms.onlinezoostore.controllers;

import jakarta.validation.Valid;
import kms.onlinezoostore.dto.ColorDto;
import kms.onlinezoostore.services.ColorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = ColorController.REST_URL)
//@Api(description = "Controller for colors")
public class ColorController {
    static final String REST_URL = "/api/v1/colors";
    private final ColorService colorService;

    @Autowired
    public ColorController(ColorService colorService) {
        this.colorService = colorService;
    }

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
