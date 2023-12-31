package kms.onlinezoostore.services.files.images;

import kms.onlinezoostore.dto.AttachedFileDto;
import org.springframework.web.multipart.MultipartFile;

public interface SingleImageOwnerService {

    AttachedFileDto uploadImageByOwnerId(Long ownerId, MultipartFile image);

    void deleteImageByOwnerId(Long ownerId);
}
