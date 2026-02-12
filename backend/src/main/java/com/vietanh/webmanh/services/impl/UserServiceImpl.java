package com.vietanh.webmanh.services.impl;

import com.vietanh.webmanh.constants.ErrorCode;
import com.vietanh.webmanh.dbs.postgres.models.User;
import com.vietanh.webmanh.dbs.postgres.repositories.UserRepository;
import com.vietanh.webmanh.dtos.requests.UpdateUserRequest;
import com.vietanh.webmanh.dtos.responses.PageResponse;
import com.vietanh.webmanh.dtos.responses.UserResponse;
import com.vietanh.webmanh.exception.AppException;
import com.vietanh.webmanh.mappers.UserMapper;
import com.vietanh.webmanh.services.UserService;
import com.vietanh.webmanh.utils.AuthUtil;
import com.vietanh.webmanh.utils.ImageUtil;
import com.vietanh.webmanh.utils.PathUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    UserMapper userMapper;

    @NonFinal
    @Value("${app.image-root}")
    String imageRoot;

    @Override
    public PageResponse<UserResponse> getUsers(Pageable pageable) {

        Page<User> page = userRepository.findAll(pageable);

        return PageResponse.<UserResponse>builder()
                .currentPage(page.getNumber())
                .totalPages(page.getTotalPages())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .data(
                        page.getContent()
                                .stream()
                                .map(this::mapToUserResponse)
                                .toList()
                )
                .build();
    }


    @Override
    public UserResponse getMyInfo() {
        Integer userId = AuthUtil.getCurrentUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return this.mapToUserResponse(user);
    }

    @Override
    public UserResponse updateMyInfo(UpdateUserRequest request) {
        Integer userId = AuthUtil.getCurrentUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new AppException(ErrorCode.USER_NOT_EXISTED)
                );

        userMapper.updateUser(user, request);

        // Xử lý upload ảnh
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            if(!ImageUtil.isValidImage(request.getImage()))
                throw new AppException(ErrorCode.NOT_IMAGE_FILE_TYPE);

            Path relativePath = saveUserImage(userId, request.getImage(), user.getAvatar());
            user.setAvatar(relativePath.toString());
        }

        return this.mapToUserResponse(userRepository.save(user));
    }

    private UserResponse mapToUserResponse(User user) {
        UserResponse response = userMapper.toUserResponse(user);

        if (user.getAvatar() != null) {
            response.setAvatar(PathUtil.toUrlPath(user.getAvatar()));
        }

        return response;
    }

    private Path saveUserImage(Integer userId, MultipartFile file, String oldImageUrl) {
        try {
            // delete old avatar
            if (oldImageUrl != null && !oldImageUrl.isBlank()) {
                deleteOldImage(oldImageUrl);
            }
            Path root = Paths.get(imageRoot);

            String originalName = Objects.requireNonNull(file.getOriginalFilename());
            String ext = originalName.substring(originalName.lastIndexOf("."));

            String randomId = UUID.randomUUID().toString();
            String fileName = randomId + ext;

            Path userDir = Paths.get(imageRoot, "users");
            Files.createDirectories(userDir);

            Path filePath = userDir.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return root.relativize(filePath);

        } catch (IOException e) {
            throw new AppException(ErrorCode.REQUIRED_IMAGE);
        }
    }

    private void deleteOldImage(String imageUrl) {
        try {
            Path imagePath = Paths.get(imageRoot, imageUrl);

            if (Files.exists(imagePath)) {
                Files.delete(imagePath);
            }
        } catch (IOException e) {

            log.error("Failed to delete old image: {}", imageUrl, e);
        }
    }


}
