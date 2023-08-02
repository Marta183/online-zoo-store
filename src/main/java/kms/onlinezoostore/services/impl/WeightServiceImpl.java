package kms.onlinezoostore.services.impl;

import kms.onlinezoostore.entities.Weight;
import kms.onlinezoostore.exceptions.EntityNotFoundException;
import kms.onlinezoostore.repositories.WeightRepository;
import kms.onlinezoostore.utils.UniqueFieldService;
import kms.onlinezoostore.services.WeightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class WeightServiceImpl implements WeightService {

    private final WeightRepository weightRepository;
    private final UniqueFieldService uniqueFieldService;
    private static final String ENTITY_CLASS_NAME = "Weight";

    @Autowired
    public WeightServiceImpl(WeightRepository weightRepository, UniqueFieldService uniqueFieldService) {
        this.weightRepository = weightRepository;
        this.uniqueFieldService = uniqueFieldService;
    }

    @Override
    public Weight findById(Long id) {
        Weight weight = weightRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));
        // log
        return weight;
    }

    @Override
    public List<Weight> findAll() {
        return weightRepository.findAll();
    }

    @Override
    @Transactional
    public Weight create(Weight weight) {
        // log
        uniqueFieldService.checkIsFieldValueUniqueOrElseThrow(weightRepository, "name", weight.getName());
        // log
        Weight savedWeight = weightRepository.save(weight);
        // log
        return savedWeight;
    }

    @Override
    @Transactional
    public Weight update(Long id, Weight updatedWeight) {
        // log
        Weight existingWeight = weightRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));
        // log
        if (!existingWeight.getName().equals(updatedWeight.getName())) {
            uniqueFieldService.checkIsFieldValueUniqueOrElseThrow(weightRepository, "name", updatedWeight.getName());
        }
        // log
        updatedWeight.setId(id);
        return weightRepository.save(updatedWeight);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        // log
        weightRepository.deleteById(id);
    }
}
