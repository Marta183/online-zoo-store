package kms.onlinezoostore.utils;

import kms.onlinezoostore.exceptions.EntityDuplicateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

@Service
public class UniqueFieldServiceImpl implements UniqueFieldService {

    private final Logger log = LoggerFactory.getLogger(UniqueFieldServiceImpl.class);

    @Override
    public <E, K, R extends JpaRepository<E, K> & JpaSpecificationExecutor<E>> boolean isFieldValueUnique(
            R repository, String fieldName, Object fieldValue
    ) {
        if (fieldValue == null)
            return true; // empty values are always unique

        log.debug("Checking uniqueness for {} of field '{}' with value '{}'", repository.getClass(), fieldName, fieldValue);

        Specification<E> spec = (root, query, criteriaBuilder) -> {
            if (fieldValue instanceof String)
                return criteriaBuilder.equal(criteriaBuilder.lower(root.get(fieldName)), fieldValue.toString().trim().toLowerCase());
            else
                return criteriaBuilder.equal(root.get(fieldName), fieldValue);
        };

        long countOfExistingData = repository.count(spec);
        log.debug("Field {} with value {} found in quantity: {}", fieldName, fieldValue, countOfExistingData);

        return countOfExistingData == 0;
    }

    @Override
    public <E, K, R extends JpaRepository<E, K> & JpaSpecificationExecutor<E>> void checkIsFieldValueUniqueOrElseThrow(
            R repository, String fieldName, Object fieldValue
    ) {

        if (!isFieldValueUnique(repository, fieldName, fieldValue)) {
            throw new EntityDuplicateException(fieldName, fieldValue.toString());
        }
    }

}
