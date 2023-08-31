package kms.onlinezoostore.services.files;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import kms.onlinezoostore.exceptions.files.FileNotFoundException;
import kms.onlinezoostore.exceptions.files.FileUploadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileServiceAWSStrategy implements FileServiceStrategy {

    @Value("${aws.bucket.name}")
    private String bucketName;
    private final AmazonS3 amazonS3Client;
    private static final String SERVICE_NAME = "AmazonS3";

    @Override
    public List<String> getAllFilesPath() {
        log.debug("Finding all file paths in {}:{}", SERVICE_NAME, bucketName);

        List<String> images = new ArrayList<>();
        ListObjectsV2Result result = amazonS3Client.listObjectsV2(bucketName);
        for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
            images.add(objectSummary.getKey());
        }

        log.debug("Found {} file paths in {}:{}", images.size(), SERVICE_NAME, bucketName);
        return images;
    }

    @Override
    public Map<String, String> uploadFile(MultipartFile file) {
        log.debug("Uploading file {} to {}:{}", file.getOriginalFilename(), SERVICE_NAME, bucketName);

        String fileName = generateUniqueFileName(file.getOriginalFilename());

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());
//        metadata.setUserMetadata();

        try {
            amazonS3Client.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), metadata));
        } catch (AmazonServiceException e) {
            throw new FileUploadException(e.getMessage());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to upload the file: " + e.getMessage());
        }

        String filePath = amazonS3Client.getUrl(bucketName, fileName).toString();

        Map<String,String> result = new HashMap<>();
        result.put("filePath", filePath);
        result.put("fileName", fileName);

        log.debug("Uploaded file {} to {}:{} with file path {}", file.getOriginalFilename(), SERVICE_NAME, bucketName, filePath);
        return result;
    }

    @Override
    public void deleteFile(String fileName) {
        log.debug("Deleting file {} from {}:{}", fileName, SERVICE_NAME, bucketName);

        if (!amazonS3Client.doesObjectExist(bucketName, fileName)) {
            throw new FileNotFoundException("File not found in bucket by file name: " + fileName);
        }

        amazonS3Client.deleteObject(new DeleteObjectRequest(bucketName, fileName));
        log.debug("Deleted file {} from {}:{}", fileName, SERVICE_NAME, bucketName);
    }

    private String generateUniqueFileName(String originalFilename) {
        return new LocalDateTime() + "-" + originalFilename.replace(" ", "_");
    }
}
