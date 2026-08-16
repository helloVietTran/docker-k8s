package com.viettran.reading_story_web.service;

import java.io.IOException;
import java.util.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.viettran.reading_story_web.exception.AppException;
import com.viettran.reading_story_web.exception.ErrorCode;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileService {
    final S3Client s3Client;

    @Value("${app.s3.bucket-name}")
    String bucketName;

    @Value("${app.s3.region}")
    String region;

    List<String> ALLOWED_IMAGE_TYPES = Arrays.asList("image/jpeg", "image/png", "image/gif");

    public static String buildObjectKey(String folderName, String fileName) {
        String normalizedFolder = folderName == null ? "" : folderName.trim().replace('\\', '/');
        normalizedFolder = normalizedFolder.replaceAll("^/+", "").replaceAll("/+$", "");
        String normalizedFileName =
                fileName == null || fileName.isBlank() ? UUID.randomUUID().toString() : fileName;

        if (normalizedFolder.isEmpty()) {
            return normalizedFileName;
        }

        return normalizedFolder + "/" + normalizedFileName;
    }

    public static String buildPublicUrl(String bucketName, String region, String objectKey) {
        String normalizedBucket = bucketName == null || bucketName.isBlank() ? "vcomic-storage" : bucketName.trim();
        String normalizedRegion = region == null || region.isBlank() ? "ap-southeast-1" : region.trim();
        String normalizedKey =
                objectKey == null ? "" : objectKey.trim().replace('\\', '/').replaceAll("^/+", "");

        return "https://" + normalizedBucket + ".s3." + normalizedRegion + ".amazonaws.com/" + normalizedKey;
    }

    public String uploadFile(MultipartFile file, String folderName) throws IOException {
        try {
            validateFileType(file);
            String fileName = getSafeFileName(file, "file");
            String objectKey = buildObjectKey(folderName, fileName);

            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(objectKey)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            return buildPublicUrl(bucketName, region, objectKey);
        } catch (IOException e) {
            throw new AppException(ErrorCode.FAILED_UPLOAD_FILE);
        }
    }

    public List<Map<String, String>> uploadFiles(List<MultipartFile> files, String folderName) throws IOException {
        try {
            for (MultipartFile file : files) {
                validateFileType(file);
            }

            List<Map<String, String>> results = new ArrayList<>();
            for (MultipartFile file : files) {
                String fileName = getSafeFileName(file, "file");
                String objectKey = buildObjectKey(folderName, fileName);

                s3Client.putObject(
                        PutObjectRequest.builder()
                                .bucket(bucketName)
                                .key(objectKey)
                                .contentType(file.getContentType())
                                .build(),
                        RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

                results.add(Map.of("fileName", fileName, "fileUrl", buildPublicUrl(bucketName, region, objectKey)));
            }

            return results;
        } catch (IOException e) {
            throw new AppException(ErrorCode.FAILED_UPLOAD_FILE);
        }
    }

    private String getSafeFileName(MultipartFile file, String fallbackName) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            return fallbackName + UUID.randomUUID();
        }

        String sanitized = originalFilename.replace('\\', '/');
        int lastSeparator = sanitized.lastIndexOf('/');
        if (lastSeparator >= 0) {
            sanitized = sanitized.substring(lastSeparator + 1);
        }
        return sanitized;
    }

    void validateFileType(MultipartFile file) {
        String contentType = file.getContentType();

        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType))
            throw new AppException(ErrorCode.NOT_IMAGE_FILE_TYPE);
    }
}
