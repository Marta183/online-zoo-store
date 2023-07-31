package kms.onlinezoostore.services;

import kms.onlinezoostore.entities.Age;

import java.util.List;

public interface AgeService {

    Age findById(Long id);
    List<Age> findAll();

    Age create(Age age);
    Age update(Long id, Age age);
    void deleteById(Long id);
}
