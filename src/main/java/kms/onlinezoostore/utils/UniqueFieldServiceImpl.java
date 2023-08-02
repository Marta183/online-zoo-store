package kms.onlinezoostore.utils;

import kms.onlinezoostore.exceptions.EntityDuplicateException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

@Service
public class UniqueFieldServiceImpl implements UniqueFieldService {

    @Override
    public <E, K, R extends JpaRepository<E, K> & JpaSpecificationExecutor<E>> boolean isFieldValueUnique(
            R repository, String fieldName, Object fieldValue
    ) {
        if (fieldValue == null)
            return true; // empty values are always unique

        Specification<E> spec = (root, query, criteriaBuilder) -> {
            if (fieldValue instanceof String)
                return criteriaBuilder.equal(criteriaBuilder.lower(root.get(fieldName)), fieldValue.toString().trim().toLowerCase());
            else
                return criteriaBuilder.equal(root.get(fieldName), fieldValue);
        };

        return repository.count(spec) == 0;
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
