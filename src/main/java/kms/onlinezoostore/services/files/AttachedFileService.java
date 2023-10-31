package kms.onlinezoostore.services.files;

import kms.onlinezoostore.dto.AttachedFileDto;
import kms.onlinezoostore.services.files.images.AttachedImageOwner;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@Service
public interface AttachedFileService {

    Set<AttachedFileDto> findAll();
    Set<AttachedFileDto> findAllByOwner(AttachedImageOwner owner);
    AttachedFileDto findByIdAndOwner(Long imageId, AttachedImageOwner owner);

    Set<AttachedFileDto> uploadFilesByOwner(AttachedImageOwner owner, List<MultipartFile> multipartFiles);
    AttachedFileDto uploadFileByOwner(AttachedImageOwner owner, MultipartFile multipartFile);

    void deleteAllByOwner(AttachedImageOwner owner);
    void deleteByIdAndOwner(Long imageId, AttachedImageOwner owner);

    AttachedFileDto replaceFileByOwner(AttachedImageOwner owner, MultipartFile newImage);
}
