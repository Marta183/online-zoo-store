package kms.onlinezoostore.services.impl;

import kms.onlinezoostore.entities.Age;
import kms.onlinezoostore.repositories.AgeRepository;
import kms.onlinezoostore.services.AgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AgeServiceImpl implements AgeService {

    private final AgeRepository ageRepository;
    @Autowired
    public AgeServiceImpl(AgeRepository ageRepository) {
        this.ageRepository = ageRepository;
    }

    @Override
    public Age findById(Long id) {
        return ageRepository.findById(id).orElse(null);
    }

    @Override
    public List<Age> findAll() {
        return ageRepository.findAll();
    }

    @Override
    @Transactional
    public Age create(Age age) {
        return ageRepository.save(age);
    }

    @Override
    @Transactional
    public Age update(Long id, Age updatedAge) {
        updatedAge.setId(id);
        return ageRepository.save(updatedAge);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        ageRepository.deleteById(id);
    }
}
