package kms.onlinezoostore.services.files.images;

import kms.onlinezoostore.dto.AttachedFileDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

public interface MultipleImagesOwnerService {

    AttachedFileDto findImageByIdAndOwnerId(Long imageId, Long ownerId);

    Set<AttachedFileDto> uploadImagesByOwnerId(Long ownerId, List<MultipartFile> images);

    void deleteAllImagesByOwnerId(Long ownerId);

    void deleteImageByIdAndOwnerId(Long imageId, Long ownerId);
}
