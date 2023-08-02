package kms.onlinezoostore.utils;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UniqueFieldService {

    <E, K, R extends JpaRepository<E, K> & JpaSpecificationExecutor<E>> boolean isFieldValueUnique(
            R repository,
            String fieldName,
            Object fieldValue);

    <E, K, R extends JpaRepository<E, K> & JpaSpecificationExecutor<E>> void checkIsFieldValueUniqueOrElseThrow(
            R repository,
            String fieldName,
            Object fieldValue);
}
