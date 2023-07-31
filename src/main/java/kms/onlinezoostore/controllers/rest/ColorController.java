package kms.onlinezoostore.controllers.rest;

import kms.onlinezoostore.entities.Color;
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
    public List<Color> findAll() {
        return colorService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Color findById(@PathVariable Long id) {
        return colorService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Color create(@RequestBody Color color) {
        return colorService.create(color);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Color update(@PathVariable Long id, @RequestBody Color color) {
        return colorService.update(id, color);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        colorService.deleteById(id);
    }
}
