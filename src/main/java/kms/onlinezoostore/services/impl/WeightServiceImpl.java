package kms.onlinezoostore.services.impl;

import kms.onlinezoostore.entities.Weight;
import kms.onlinezoostore.repositories.WeightRepository;
import kms.onlinezoostore.services.WeightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class WeightServiceImpl implements WeightService {

    private final WeightRepository weightRepository;
    @Autowired
    public WeightServiceImpl(WeightRepository weightRepository) {
        this.weightRepository = weightRepository;
    }

    @Override
    public Weight findById(Long id) {
        return weightRepository.findById(id).orElse(null);
    }

    @Override
    public List<Weight> findAll() {
        return weightRepository.findAll();
    }

    @Override
    @Transactional
    public Weight create(Weight weight) {
        return weightRepository.save(weight);
    }

    @Override
    @Transactional
    public Weight update(Long id, Weight updatedWeight) {
        updatedWeight.setId(id);
        return weightRepository.save(updatedWeight);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        weightRepository.deleteById(id);
    }
}
