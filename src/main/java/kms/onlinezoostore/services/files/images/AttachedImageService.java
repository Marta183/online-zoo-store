package kms.onlinezoostore.services.files.images;

import kms.onlinezoostore.dto.AttachedFileDto;
import kms.onlinezoostore.dto.mappers.AttachedFileMapper;
import kms.onlinezoostore.entities.AttachedFile;
import kms.onlinezoostore.exceptions.EntityNotFoundException;
import kms.onlinezoostore.exceptions.files.FileEmptyException;
import kms.onlinezoostore.exceptions.files.InvalidFileException;
import kms.onlinezoostore.repositories.AttachedFileRepository;
import kms.onlinezoostore.services.files.AttachedFileService;
import kms.onlinezoostore.services.files.FileServiceStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttachedImageService implements AttachedFileService {
    private final AttachedFileMapper attachedFileMapper;
    private final AttachedFileRepository attachedFileRepository;
    private final FileServiceStrategy fileService;

    private static final String ENTITY_CLASS_NAME = "ATTACHED_FILE";

    @Override
    public Set<AttachedFileDto> findAll() {
        return attachedFileRepository.findAll()
                .stream().map(attachedFileMapper::mapToDto)
                .collect(Collectors.toSet());
    }

    @Override
    public AttachedFileDto findByIdAndOwner(Long imageId, AttachedImageOwner owner) {
        /// TODO: почему тут фильтрация, а в методе deleteByIdAndOwner() реализовано без фильтрации сразу с использ.нужного метода сервиса ???
//        return attachedFileRepository.findById(imageId)
//                .map(attachedFileMapper::mapToDtoWithOwner)
//                .filter(attachedFileDto -> attachedFileDto.getOwner().equals(owner))
//                .orElseThrow(() -> new EntityNotFoundException(entityNotFoundMessage(imageId, owner)));
        return attachedFileRepository.findByIdAndOwnerIdAndOwnerClass(imageId, owner.getId(), owner.getImageOwnerClassName())
                .map(attachedFileMapper::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException(messageEntityNotFound(imageId, owner)));
    }

//    @Override
//    public AttachedFileDto findByFilePathAndOwner(String filePath, AttachedImageOwner owner) {
//        return attachedFileRepository.findByFilePathAndOwnerIdAndOwnerClass(filePath, owner.getId(), owner.getImageOwnerClassName())
//                .map(attachedFileMapper::mapToDtoWithOwner)
//                .orElseThrow(() -> new EntityNotFoundException(messageEntityNotFound(filePath, owner)));
//    }

    @Override
    public Set<AttachedFileDto> findAllByOwner(AttachedImageOwner owner) {
        return attachedFileRepository.findAllByOwnerIdAndOwnerClass(owner.getId(), owner.getImageOwnerClassName())
                .stream().map(attachedFileMapper::mapToDto)
                .collect(Collectors.toSet());
    }

    @Override
    public AttachedFileDto findFirstByOwner(AttachedImageOwner owner) {
        return findAllByOwner(owner).stream().findFirst().orElse(null);
    }

    @Override
    @Transactional
    public Set<AttachedFileDto> uploadFilesByOwner(AttachedImageOwner owner, List<MultipartFile> images) {
        Set<AttachedFileDto> uploadedImages = new HashSet<>();
        for (MultipartFile image : images) {
            uploadedImages.add(uploadFileByOwner(owner, image));
        }
        return uploadedImages;
    }

    @Override
    @Transactional
    public AttachedFileDto uploadFileByOwner(AttachedImageOwner owner, MultipartFile image) {

        // validating
        controlFileValidOrThrowException(image);

        // loading into remote file service
        Map<String, String> fileData = fileService.uploadFile(image);

        // saving in DB
        AttachedFile attachedFile = new AttachedFile();
        attachedFile.setFilePath(fileData.get("filePath"));
        attachedFile.setFileName(fileData.get("fileName"));
        attachedFile.setOwnerId(owner.getId());
        attachedFile.setOwnerClass(owner.getImageOwnerClassName());

        AttachedFile savedFile = attachedFileRepository.save(attachedFile);

        AttachedFileDto savedFileDto = attachedFileMapper.mapToDto(savedFile);
        //log
        return savedFileDto;
    }

    @Override
    @Transactional
    public void deleteAllByOwner(AttachedImageOwner owner) {
        List<AttachedFile> images = attachedFileRepository.findAllByOwnerIdAndOwnerClass(owner.getId(), owner.getImageOwnerClassName());

        for (AttachedFile image : images) {
            fileService.deleteFile(image.getFileName());
            attachedFileRepository.deleteById(image.getId());
        }
    }

    @Override
    @Transactional
    public void deleteByIdAndOwner(Long imageId, AttachedImageOwner owner) {
        AttachedFile image = attachedFileRepository.findByIdAndOwnerIdAndOwnerClass(imageId, owner.getId(), owner.getImageOwnerClassName())
                .orElseThrow(() -> new EntityNotFoundException(messageEntityNotFound(imageId, owner)));

        fileService.deleteFile(image.getFileName());
        attachedFileRepository.deleteById(imageId);
    }

    private void controlFileValidOrThrowException(MultipartFile multipartFile) {
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
            throw new InvalidFileException("Uploaded file has unsupportable extension. " +
                    "Choose one of the permitted extension: " + AllowedImageExtensions.valuesAsString());
        }
    }

    private String getFileExtension(String originalFilename) {
        return originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
    }

    private String messageEntityNotFound(Long entityId, AttachedImageOwner owner) {
        return new StringBuilder(ENTITY_CLASS_NAME)
                .append(" not found with id=").append(entityId)
                .append(" and owner ").append(owner.getImageOwnerClassName())
                .append("(id=").append(owner.getId()).append(")")
                .toString();
    }

//    private String messageEntityNotFound(String path, AttachedImageOwner owner) {
//        return ENTITY_CLASS_NAME + " not found by path=" + path + " and owner " + owner;
//    }
}
