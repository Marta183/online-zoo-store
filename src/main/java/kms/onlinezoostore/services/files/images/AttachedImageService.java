package kms.onlinezoostore.services.files.images;

import kms.onlinezoostore.dto.AttachedFileDto;
import kms.onlinezoostore.dto.mappers.AttachedFileMapper;
import kms.onlinezoostore.entities.AttachedFile;
import kms.onlinezoostore.exceptions.EntityNotFoundException;
import kms.onlinezoostore.exceptions.files.FileEmptyException;
import kms.onlinezoostore.exceptions.files.FileNotFoundException;
import kms.onlinezoostore.exceptions.files.FileUploadException;
import kms.onlinezoostore.exceptions.files.InvalidFileException;
import kms.onlinezoostore.repositories.AttachedFileRepository;
import kms.onlinezoostore.services.files.AttachedFileService;
import kms.onlinezoostore.services.files.FileServiceStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttachedImageService implements AttachedFileService {
    private final AttachedFileMapper attachedFileMapper;
    private final AttachedFileRepository attachedFileRepository;
    private final FileServiceStrategy fileService;

    private static final String SERVICE_NAME = "ATTACHED_IMAGE";

    @Override
    public Set<AttachedFileDto> findAll() {
        log.debug("Finding all {} in repository", SERVICE_NAME);

        Set<AttachedFileDto> images = attachedFileRepository.findAll()
                .stream().map(attachedFileMapper::mapToDto)
                .collect(Collectors.toSet());

        log.debug("Found {} {} in repository", images.size(), SERVICE_NAME);
        return images;
    }

    @Override
    public AttachedFileDto findByIdAndOwner(Long imageId, AttachedImageOwner owner) {
        log.debug("Finding {} by ID {} by owner {}", SERVICE_NAME, imageId, owner.toStringImageOwner());

        AttachedFileDto attachedImageDto = attachedFileRepository.findByIdAndOwnerIdAndOwnerClass(imageId, owner.getId(), owner.getImageOwnerClassName())
                .map(attachedFileMapper::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException(messageEntityNotFound(imageId, owner)));

        log.debug("Found {} by ID {} by owner {}", SERVICE_NAME, imageId, owner.toStringImageOwner());
        return attachedImageDto;
    }

    @Override
    public Set<AttachedFileDto> findAllByOwner(AttachedImageOwner owner) {
        log.debug("Finding all {} by owner {}", SERVICE_NAME, owner.toStringImageOwner());

        Set<AttachedFileDto> images = attachedFileRepository.findAllByOwnerIdAndOwnerClass(owner.getId(), owner.getImageOwnerClassName())
                .stream().map(attachedFileMapper::mapToDto)
                .collect(Collectors.toSet());

        log.debug("Found {} {} by owner {}", images.size(), SERVICE_NAME, owner.toStringImageOwner());
        return images;
    }

    @Override
    @Transactional
    public Set<AttachedFileDto> uploadFilesByOwner(AttachedImageOwner owner, List<MultipartFile> images) {
        log.debug("Uploading {} {} for owner {}", SERVICE_NAME, images.size(), owner.toStringImageOwner());
        
        Set<AttachedFileDto> uploadedImages = new HashSet<>();
        for (MultipartFile image : images) {
            uploadedImages.add(uploadFileByOwner(owner, image));
        }

        log.debug("Uploaded {} {} for owner {}", uploadedImages.size(), SERVICE_NAME, owner.toStringImageOwner());
        return uploadedImages;
    }

    @Override
    @Transactional
    public AttachedFileDto uploadFileByOwner(AttachedImageOwner owner, MultipartFile image) {
        log.debug("Uploading {} for owner {}", SERVICE_NAME, owner.toStringImageOwner());

        // validating
        verifyFileValidOrThrowException(image);

        // loading into remote file service
        AttachedFileDto savedFileDto = uploadFileToFileSystemAndSaveInDB(owner, image);
        log.debug("New {} saved in DB with ID {}", SERVICE_NAME, savedFileDto.getId());

        return savedFileDto;
    }

    private AttachedFileDto uploadFileToFileSystemAndSaveInDB(AttachedImageOwner owner, MultipartFile image) {
        log.debug("Uploading {} for owner {}", SERVICE_NAME, owner.toStringImageOwner());

        // loading into remote file service
        log.debug("Uploading {} by owner {} to file system", SERVICE_NAME, owner.toStringImageOwner());
        Map<String, String> fileData = fileService.uploadFile(image);

        // saving in DB
        AttachedFile attachedFile = new AttachedFile();
        attachedFile.setFilePath(fileData.get("filePath"));
        attachedFile.setFileName(fileData.get("fileName"));
        attachedFile.setOwnerId(owner.getId());
        attachedFile.setOwnerClass(owner.getImageOwnerClassName());

        AttachedFile savedFile = attachedFileRepository.save(attachedFile);
        log.debug("New {} saved in DB with ID {}", SERVICE_NAME, savedFile.getId());

        return attachedFileMapper.mapToDto(savedFile);
    }

    @Override
    @Transactional
    public AttachedFileDto replaceFileByOwner(AttachedImageOwner owner, MultipartFile newImage) {
        log.debug("Replacing existing image for {} ", owner.toStringImageOwner());

        verifyFileValidOrThrowException(newImage);

        // delete existing image
        try {
            deleteAllByOwner(owner);
        } catch (FileNotFoundException ex) {
            throw new FileUploadException("Cannot replace an existing image because of: " + ex.getMessage());
        }

        // upload new image
        AttachedFileDto uploadedImage = uploadFileToFileSystemAndSaveInDB(owner, newImage);
        log.debug("Replaced existing image for {} ", owner.toStringImageOwner());

        return uploadedImage;
    }

    @Override
    @Transactional
    public void deleteAllByOwner(AttachedImageOwner owner) {
        log.debug("Deleting all {} for owner {}", SERVICE_NAME, owner.toStringImageOwner());
        
        List<AttachedFile> images = attachedFileRepository.findAllByOwnerIdAndOwnerClass(owner.getId(), owner.getImageOwnerClassName());
        log.debug("Found {} {} for owner {}", images.size(), SERVICE_NAME, owner.toStringImageOwner());

        for (AttachedFile image : images) {
            attachedFileRepository.deleteById(image.getId());
            log.debug("Deleted from repository {} by ID {} for owner {}", SERVICE_NAME, image.getId(), owner.toStringImageOwner());

            fileService.deleteFile(image.getFileName());
            log.debug("Deleted from file system {} by ID {} for owner {}", SERVICE_NAME, image.getId(), owner.toStringImageOwner());
        }
    }

    @Override
    @Transactional
    public void deleteByIdAndOwner(Long imageId, AttachedImageOwner owner) {
        log.debug("Deleting {} by ID {} for owner {}", SERVICE_NAME, imageId, owner.toStringImageOwner());
        
        AttachedFile image = attachedFileRepository.findByIdAndOwnerIdAndOwnerClass(imageId, owner.getId(), owner.getImageOwnerClassName())
                .orElseThrow(() -> new EntityNotFoundException(messageEntityNotFound(imageId, owner)));

        attachedFileRepository.deleteById(imageId);
        log.debug("Deleted from DB {} by ID {} for owner {}", SERVICE_NAME, imageId, owner.toStringImageOwner());

        fileService.deleteFile(image.getFileName());
        log.debug("Deleted from file system {} by ID {} for owner {}", SERVICE_NAME, imageId, owner.toStringImageOwner());
    }

    private void verifyFileValidOrThrowException(MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            throw new FileEmptyException("Uploaded file is empty. Cannot save an empty file.");
        }

        String originalFilename = multipartFile.getOriginalFilename();
        if (Objects.isNull(originalFilename) || originalFilename.trim().isBlank()){
            throw new InvalidFileException("Uploaded file name is empty. Cannot save file.");
        }

        String fileExtension = getFileExtension(originalFilename);
        boolean isValidExtension = Arrays.stream(AllowedImageExtensions.values())
                    .anyMatch(extension -> extension.name().equalsIgnoreCase(fileExtension));
        if (!isValidExtension) {
            throw new InvalidFileException("Uploaded file has unsupportable extension " + fileExtension + ". \n" +
                    "Choose one of the permitted extension: " + AllowedImageExtensions.valuesAsString());
        }
    }

    private String getFileExtension(String originalFilename) {
        return originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
    }

    private String messageEntityNotFound(Long entityId, AttachedImageOwner owner) {
        return new StringBuilder(SERVICE_NAME)
                .append(" not found with id=").append(entityId)
                .append(" and owner ").append(owner.getImageOwnerClassName())
                .append("(id=").append(owner.getId()).append(")")
                .toString();
    }
}
