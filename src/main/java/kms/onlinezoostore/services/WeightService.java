package kms.onlinezoostore.services;


import kms.onlinezoostore.entities.Weight;

import java.util.List;

public interface WeightService {

    Weight findById(Long id);
    List<Weight> findAll();

    Weight create(Weight weight);
    Weight update(Long id, Weight weight);
    void deleteById(Long id);
}
