package kms.onlinezoostore.controllers;

import kms.onlinezoostore.dto.ConstantDto;
import kms.onlinezoostore.entities.enums.ConstantKeys;
import kms.onlinezoostore.services.ConstantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = ConstantController.REST_URL)
public class ConstantController {
    static final String REST_URL = "/api/v1/constants";

    private final ConstantService constantService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ConstantDto> findAll() {
        return constantService.findAll();
    }

    @GetMapping("/{key}")
    @ResponseStatus(HttpStatus.OK)
    public ConstantDto findValueByKey(@PathVariable ConstantKeys key) {
        return constantService.findByKey(key);
    }

    @PutMapping("/{key}")
    @ResponseStatus(HttpStatus.OK)
    public void updateValue(@PathVariable ConstantKeys key, @RequestParam(name = "value") Object updatedValue) {
        constantService.updateValue(key, updatedValue);
    }
}
