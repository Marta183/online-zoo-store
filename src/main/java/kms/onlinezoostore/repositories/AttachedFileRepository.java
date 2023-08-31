package kms.onlinezoostore.repositories;

import kms.onlinezoostore.entities.AttachedFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttachedFileRepository extends JpaRepository<AttachedFile, Long>, JpaSpecificationExecutor<AttachedFile> {

    List<AttachedFile> findAllByOwnerIdAndOwnerClass(Long ownerId, String ownerClass);

    Optional<AttachedFile> findByIdAndOwnerIdAndOwnerClass(Long imageId, Long ownerId, String ownerClass);
}
