package kms.onlinezoostore.services.files;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface FileServiceStrategy {

    List<String> getAllFilesPath();

    Map<String, String> uploadFile(MultipartFile file);

    void deleteFile(String fileName);
}
