package com.viettran.reading_story_web.service;

import java.io.IOException;
import java.util.*;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.viettran.reading_story_web.exception.AppException;
import com.viettran.reading_story_web.exception.ErrorCode;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileService {
    Cloudinary cloudinary;

    List<String> ALLOWED_IMAGE_TYPES = Arrays.asList("image/jpeg", "image/png", "image/gif");

    public String uploadFile(MultipartFile file, String folderName) throws IOException {
        try {
            validateFileType(file);

            Map<?, ?> uploadOptions = ObjectUtils.asMap("folder", folderName);
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadOptions);

            return uploadResult.get("url").toString();
        } catch (IOException e) {
            throw new AppException(ErrorCode.FAILED_UPLOAD_FILE);
        }
    }

    public List<Map<String, String>> uploadFiles(List<MultipartFile> files, String folderName) throws IOException {
        try {
            // validate file type
            for (MultipartFile file : files) {
                validateFileType(file);
            }

            Map<?, ?> uploadOptions = ObjectUtils.asMap("folder", folderName);
            List<Map<String, String>> results = new ArrayList<>();

            for (MultipartFile file : files) {
                Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadOptions);

                // lấy tên ảnh
                String publicId = (String) uploadResult.get("public_id");
                publicId = publicId.contains("/") ? publicId.substring(publicId.lastIndexOf("/") + 1) : publicId;

                results.add(ObjectUtils.asMap("fileName", publicId, "fileUrl", uploadResult.get("url")));
            }

            return results;
        } catch (IOException e) {
            throw new AppException(ErrorCode.FAILED_UPLOAD_FILE);
        }
    }

    void validateFileType(MultipartFile file) {
        String contentType = file.getContentType();

        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType))
            throw new AppException(ErrorCode.NOT_IMAGE_FILE_TYPE);
    }
}
