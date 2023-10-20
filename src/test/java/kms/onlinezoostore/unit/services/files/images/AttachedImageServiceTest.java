package kms.onlinezoostore.unit.services.files.images;

import kms.onlinezoostore.dto.AttachedFileDto;
import kms.onlinezoostore.dto.BrandDto;
import kms.onlinezoostore.dto.mappers.AttachedFileMapper;
import kms.onlinezoostore.entities.AttachedFile;
import kms.onlinezoostore.exceptions.EntityNotFoundException;
import kms.onlinezoostore.exceptions.files.FileEmptyException;
import kms.onlinezoostore.exceptions.files.InvalidFileException;
import kms.onlinezoostore.repositories.AttachedFileRepository;
import kms.onlinezoostore.services.files.FileServiceStrategy;
import kms.onlinezoostore.services.files.images.AttachedImageOwner;
import kms.onlinezoostore.services.files.images.AttachedImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.stream.Collectors;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;

@ExtendWith(MockitoExtension.class)
public class AttachedImageServiceTest {
    @Spy
    private AttachedFileMapper attachedFileMapper = Mappers.getMapper(AttachedFileMapper.class);
    @Mock
    private AttachedFileRepository attachedFileRepository;
    @Mock
    private FileServiceStrategy fileService;

    @InjectMocks
    private AttachedImageService attachedImageService;

    private AttachedFile attachedFile;
    private AttachedImageOwner  imageOwner;

    @BeforeEach
    public void setup() {
        attachedFile = new AttachedFile(1L, "path1", "name1", 1L, "Brand");
        imageOwner = new BrandDto(1L, "test", null);
    }

    @Test
    void findAll_ShouldReturnAttachedFileSet() {
        Set<AttachedFile> attachedFileSet = new HashSet<>() {{ add(attachedFile); }};
        attachedFileSet.add(new AttachedFile(2L, "path2", "name2", 1L, "owner1"));
        attachedFileSet.add(new AttachedFile(3L, "path3", "name3", 2L, "owner3"));

        Set<AttachedFileDto> attachedFileDtoSet = attachedFileSet.stream().map(attachedFileMapper::mapToDto).collect(Collectors.toSet()); 

        when(attachedFileRepository.findAll()).thenReturn(new ArrayList<>(attachedFileSet));

        // act
        Set<AttachedFileDto> actualAttachedFileDtoSet = attachedImageService.findAll();

        assertEquals(attachedFileDtoSet.size(), actualAttachedFileDtoSet.size(), "AttachedFileDto set size: expected and actual is not equal.");
        assertIterableEquals(attachedFileDtoSet, actualAttachedFileDtoSet, "AttachedFileDto set: expected and actual don't have the same elements in the same order.");
    }
    
    @Test
    void findAll_ShouldReturnEmptySet() {
        when(attachedFileRepository.findAll()).thenReturn(Collections.emptyList());

        Set<AttachedFileDto> actualAttachedFileDtoSet = attachedImageService.findAll();

        assertEquals(0, actualAttachedFileDtoSet.size(), "Actual attachedFileDto set is not empty");
    }


    @Test
    void findByIdAndOwner_shouldReturnAttachedFileDto_WhenExists() {
        AttachedFileDto attachedFileDto = attachedFileMapper.mapToDto(attachedFile);

        when(attachedFileRepository.findByIdAndOwnerIdAndOwnerClass(anyLong(), anyLong(), anyString()))
                .thenReturn(Optional.of(attachedFile));

        AttachedFileDto attachedFileDtoActual = attachedImageService.findByIdAndOwner(1L,  imageOwner);

        assertEquals(attachedFileDto, attachedFileDtoActual, "AttachedFileDto: expected and actual is not equal.");
    }

    @Test
    void findByIdAndOwner_ShouldThrowException_WhenNotFound() {
        when(attachedFileRepository.findByIdAndOwnerIdAndOwnerClass(anyLong(), anyLong(), anyString())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> attachedImageService.findByIdAndOwner(1L,  imageOwner));
    }

    @Test
    void uploadFileByOwner_ShouldReturnAttachedFileDto() {
        MultipartFile image = new MockMultipartFile("name1", "name1.png", "", new byte[]{0});

        Map<String,String> fileData = new HashMap<>();
        fileData.put("filePath", "testPath");
        fileData.put("fileName", "name1WithAdditionalInfo");

        when(fileService.uploadFile(image)).thenReturn(fileData);
        when(attachedFileRepository.save(any(AttachedFile.class))).thenReturn(attachedFile);

        AttachedFileDto uploadedImage = attachedImageService.uploadFileByOwner( imageOwner, image);

        assertNotNull(uploadedImage);
        assertTrue(uploadedImage.getFileName().contains(image.getName()));

        verify(attachedFileRepository).save(any(AttachedFile.class));
    }

    @Test
    void uploadFileByOwner_ShouldThrowException_WhenFileIsEmpty() {
        MultipartFile image = new MockMultipartFile("name1", "name1.png", "", new byte[0]);

        assertThrows(FileEmptyException.class, () -> attachedImageService.uploadFileByOwner( imageOwner, image));
    }

    @Test
    void uploadFileByOwner_ShouldThrowException_WhenInvalidFileName() {
        MultipartFile image = new MockMultipartFile("name1", "", "", new byte[]{0});

        assertThrows(InvalidFileException.class, () -> attachedImageService.uploadFileByOwner( imageOwner, image));
    }

    @Test
    void uploadFileByOwner_ShouldThrowException_WhenInvalidFileExtension() {
        MultipartFile image = new MockMultipartFile("name1", "name1.qqq", "", new byte[]{0});

        assertThrows(InvalidFileException.class, () -> attachedImageService.uploadFileByOwner( imageOwner, image));
    }

    @Test
    void deleteAllByOwner_WhenDataIsCorrect() {
        List<AttachedFile> images = new ArrayList<>() {{
            add(attachedFile);
            add(new AttachedFile(2L, "path2", "name2", 1L, "owner1"));
            add(new AttachedFile(3L, "path3", "name3", 2L, "owner3"));
        }};
        
        when(attachedFileRepository.findAllByOwnerIdAndOwnerClass(imageOwner.getId(), imageOwner.getImageOwnerClassName()))
                .thenReturn(images);

        attachedImageService.deleteAllByOwner(imageOwner);

        for (AttachedFile image : images) {
            verify(fileService).deleteFile(image.getFileName());
            verify(attachedFileRepository).deleteById(image.getId());
        }
    }

    @Test
    void deleteByIdAndOwner_WhenDataIsCorrect() {
        when(attachedFileRepository.findByIdAndOwnerIdAndOwnerClass(attachedFile.getId(), attachedFile.getOwnerId(), attachedFile.getOwnerClass()))
                .thenReturn(Optional.of(attachedFile));
        doNothing().when(fileService).deleteFile(attachedFile.getFileName());
        doNothing().when(attachedFileRepository).deleteById(attachedFile.getId());

        attachedImageService.deleteByIdAndOwner(attachedFile.getId(), imageOwner);

        verify(fileService).deleteFile(attachedFile.getFileName());
        verify(attachedFileRepository).deleteById(attachedFile.getId());
    }

    @Test
    void deleteByIdAndOwner_ShouldThrowException_WhenImageNotFound() {
        when(attachedFileRepository.findByIdAndOwnerIdAndOwnerClass(anyLong(), anyLong(), anyString())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> attachedImageService.deleteByIdAndOwner(1L, imageOwner));
    }
}
