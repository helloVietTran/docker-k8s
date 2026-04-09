package com.viettran.reading_story_web.service;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.viettran.reading_story_web.dto.request.*;
import com.viettran.reading_story_web.dto.response.EmailResponse;
import com.viettran.reading_story_web.dto.response.FollowResponse;
import com.viettran.reading_story_web.dto.response.UserResponse;
import com.viettran.reading_story_web.entity.mysql.*;
import com.viettran.reading_story_web.exception.AppException;
import com.viettran.reading_story_web.exception.ErrorCode;
import com.viettran.reading_story_web.mapper.LevelMapper;
import com.viettran.reading_story_web.mapper.StoryMapper;
import com.viettran.reading_story_web.mapper.UserMapper;
import com.viettran.reading_story_web.repository.jpa.*;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    StoryRepository storyRepository;
    FollowRepository followRepository;
    ResetPasswordTokenRepository resetPasswordTokenRepository;
    LevelRepository levelRepository;

    UserMapper userMapper;
    StoryMapper storyMapper;
    LevelMapper levelMapper;

    PasswordEncoder passwordEncoder;
    FileService fileService;
    EmailService emailService;

    AuthenticationService authenticationService;

    @NonFinal
    @Value("${app.folder.avatar}")
    protected String AVATAR_FOLDER;

    public UserResponse createUser(UserCreationRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isPresent()) throw new AppException(ErrorCode.USER_EXISTED);

        HashSet<Role> roles = new HashSet<>();
        roleRepository.findById("USER").ifPresent(roles::add);

        User user = userMapper.toUser(request);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(roles);
        userRepository.save(user);

        Level level = Level.builder().user(user).build();

        levelRepository.save(level);

        return userMapper.toUserResponse(user);
    }

    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();

        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        UserResponse response = userMapper.toUserResponse(user);

        response.setLevel(levelMapper.toLevelResponse(user.getLevel()));
        return response;
    }

    public UserResponse updateUser(String userId, UserUpdationRequest request) {

        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        user.setUpdatedAt(Instant.now());
        userMapper.updateUser(user, request);

        return userMapper.toUserResponse(userRepository.save(user));
    }

    public void deleteUser() {
        var context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) throw new AppException(ErrorCode.USER_NOT_EXISTED);

        userRepository.deleteByEmail(email);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getUsers() {
        return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
    }

    public UserResponse getUser(String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        UserResponse response = userMapper.toUserResponse(user);
        response.setLevel(levelMapper.toLevelResponse(user.getLevel()));

        return response;
    }

    @PreAuthorize("#id == authentication.name")
    public UserResponse uploadAvatar(String id, UploadAvatarRequest request) throws IOException {
        String url = fileService.uploadFile(request.getFile(), AVATAR_FOLDER);

        User user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        user.setImgSrc(url);
        user.setUpdatedAt(Instant.now());
        return userMapper.toUserResponse(userRepository.save(user));
    }

    public FollowResponse followStory(FollowRequest request) {
        String userId = authenticationService.getCurrentUserId();

        if (followRepository.existsByUserIdAndStoryId(userId, request.getStoryId()))
            throw new AppException(ErrorCode.FOLLOWED_STORY);

        Story story = storyRepository
                .findById(request.getStoryId())
                .orElseThrow(() -> new AppException(ErrorCode.STORY_NOT_EXISTED));

        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Follow follow = Follow.builder()
                .story(story)
                .user(user)
                .followTime(Instant.now())
                .build();
        followRepository.save(follow);

        story.setFollower(story.getFollower() + 1); // Tăng follower cho truyện
        storyRepository.save(story);

        return FollowResponse.builder()
                .story(storyMapper.toStoryResponse(story))
                .user(userMapper.toUserResponse(user))
                .build();
    }

    public void unfollowStory(Integer storyId, String followId) {
        followRepository.deleteById(followId);
        Story story =
                storyRepository.findById(storyId).orElseThrow(() -> new AppException(ErrorCode.STORY_NOT_EXISTED));

        story.setFollower(story.getFollower() - 1);
        storyRepository.save(story);
    }

    public EmailResponse forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        String rawResetPasswordToken = generateResetPasswordToken(user, 60 * 15);

        return emailService.sendResetPasswordEmail(rawResetPasswordToken, user);
    }

    public void resetPassword(ChangePasswordRequest request) {
        Optional<ResetPasswordToken> tokenOptional =
                resetPasswordTokenRepository.findFirstByUserIdAndStatusAndExpirationDateAfterOrderByCreatedDateAsc(
                        request.getUserId(), "unused", Instant.now());

        if (tokenOptional.isEmpty()) throw new AppException(ErrorCode.UNAUTHENTICATED);

        ResetPasswordToken token = tokenOptional.get();

        if (!request.getPassword().equals(request.getConfirmPassword()))
            throw new AppException(ErrorCode.INVALID_CONFIRM_PASSWORD);

        if (!passwordEncoder.matches(request.getToken(), token.getToken()))
            throw new AppException(ErrorCode.INVALID_TOKEN);

        User user = userRepository
                .findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
    }

    public void changePassword(ChangePasswordRequest request) {
        String userId = authenticationService.getCurrentUserId();

        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        if (!request.getPassword().equals(request.getConfirmPassword()))
            throw new AppException(ErrorCode.INVALID_CONFIRM_PASSWORD);

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);
    }

    public List<UserResponse> getTop10UsersByChaptersRead() {
        Pageable pageable = PageRequest.of(0, 10);
        List<User> topUsers = userRepository.findTop10UsersByChaptersRead(pageable);

        return topUsers.stream()
                .map(user -> {
                    UserResponse userResponse = userMapper.toUserResponse(user);

                    if (user.getLevel() != null) {
                        userResponse.setLevel(levelMapper.toLevelResponse(user.getLevel()));
                    }

                    return userResponse;
                })
                .toList();
    }

    String generateResetPasswordToken(User user, int expirationSeconds) {

        String rawToken = UUID.randomUUID().toString();
        String hashedToken = passwordEncoder.encode(rawToken); // Hash token

        ResetPasswordToken resetToken = new ResetPasswordToken(user, expirationSeconds);
        resetToken.setToken(hashedToken);
        resetToken.setStatus("unused");
        resetToken.setUser(user);

        resetPasswordTokenRepository.save(resetToken);

        return rawToken;
    }
}
