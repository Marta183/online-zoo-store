package kms.onlinezoostore.controllers.rest;

import jakarta.validation.Valid;
import kms.onlinezoostore.entities.Weight;
import kms.onlinezoostore.services.WeightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = WeightController.REST_URL)
//@Api(description = "Controller for weights")
public class WeightController {
    static final String REST_URL = "/api/v1/weights";
    private final WeightService weightService;

    @Autowired
    public WeightController(WeightService weightService) {
        this.weightService = weightService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Weight> findAll() {
        return weightService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Weight findById(@PathVariable Long id) {
        return weightService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Weight create(@RequestBody @Valid Weight weight) {
        return weightService.create(weight);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Weight update(@PathVariable Long id, @RequestBody @Valid Weight weight) {
        return weightService.update(id, weight);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        weightService.deleteById(id);
    }
}
