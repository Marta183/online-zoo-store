package kms.onlinezoostore.services.impl;

import kms.onlinezoostore.entities.Age;
import kms.onlinezoostore.exceptions.EntityNotFoundException;
import kms.onlinezoostore.repositories.AgeRepository;
import kms.onlinezoostore.services.AgeService;
import kms.onlinezoostore.utils.UniqueFieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AgeServiceImpl implements AgeService {

    private final AgeRepository ageRepository;
    private final UniqueFieldService uniqueFieldService;
    private static final String ENTITY_CLASS_NAME = "Age";

    @Autowired
    public AgeServiceImpl(AgeRepository ageRepository, UniqueFieldService uniqueFieldService) {
        this.ageRepository = ageRepository;
        this.uniqueFieldService = uniqueFieldService;
    }

    @Override
    public Age findById(Long id) {
        Age age = ageRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));
        // log
        return age;
    }

    @Override
    public List<Age> findAll() {
        return ageRepository.findAll();
    }

    @Override
    @Transactional
    public Age create(Age age) {
        // log
        uniqueFieldService.checkIsFieldValueUniqueOrElseThrow(ageRepository, "name", age.getName());
        // log
        Age savedAge = ageRepository.save(age);
        // log
        return savedAge;
    }

    @Override
    @Transactional
    public Age update(Long id, Age updatedAge) {
        // log
        Age existingAge = ageRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ENTITY_CLASS_NAME, id));
        // log
        if (!existingAge.getName().equals(updatedAge.getName())) {
            uniqueFieldService.checkIsFieldValueUniqueOrElseThrow(ageRepository, "name", updatedAge.getName());
        }
        // log
        updatedAge.setId(id);
        return ageRepository.save(updatedAge);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        // log
        ageRepository.deleteById(id);
    }
}
