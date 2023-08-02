package kms.onlinezoostore.controllers.rest;

import jakarta.validation.Valid;
import kms.onlinezoostore.entities.Age;
import kms.onlinezoostore.services.AgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = AgeController.REST_URL)
//@Api(description = "Controller for ages")
public class AgeController {
    static final String REST_URL = "/api/v1/ages";
    private final AgeService ageService;

    @Autowired
    public AgeController(AgeService ageService) {
        this.ageService = ageService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Age> findAll() {
        return ageService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Age findById(@PathVariable Long id) {
        return ageService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Age create(@RequestBody @Valid Age age) {
        return ageService.create(age);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Age update(@PathVariable Long id, @RequestBody @Valid Age age) {
        return ageService.update(id, age);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        ageService.deleteById(id);
    }
}
