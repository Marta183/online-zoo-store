package kms.onlinezoostore.controllers.rest;

import jakarta.validation.Valid;
import kms.onlinezoostore.entities.Material;
import kms.onlinezoostore.services.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = MaterialController.REST_URL)
//@Api(description = "Controller for materials")
public class MaterialController {
    static final String REST_URL = "/api/v1/materials";
    private final MaterialService materialService;

    @Autowired
    public MaterialController(MaterialService materialService) {
        this.materialService = materialService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Material> findAll() {
        return materialService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Material findById(@PathVariable Long id) {
        return materialService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Material create(@RequestBody @Valid Material material) {
        return materialService.create(material);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Material update(@PathVariable Long id, @RequestBody @Valid Material material) {
        return materialService.update(id, material);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        materialService.deleteById(id);
    }
}
