package com.viettran.reading_story_web.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.viettran.reading_story_web.dto.request.AvatarFrameRequest;
import com.viettran.reading_story_web.dto.response.AvatarFrameResponse;
import com.viettran.reading_story_web.entity.mysql.AvatarFrame;
import com.viettran.reading_story_web.exception.AppException;
import com.viettran.reading_story_web.exception.ErrorCode;
import com.viettran.reading_story_web.mapper.AvatarFrameMapper;
import com.viettran.reading_story_web.repository.jpa.AvatarFrameRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ShopService {
    AvatarFrameRepository avatarFrameRepository;
    AvatarFrameMapper avatarFrameMapper;

    FileService fileService;

    @NonFinal
    @Value("${app.folder.avatar-frame}")
    protected String AVATAR_FRAME_FOLDER;

    public List<AvatarFrameResponse> getAllAvatarFrames() {
        return avatarFrameRepository.findAll().stream()
                .map(avatarFrameMapper::toAvatarFrameResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public AvatarFrameResponse createAvatarFrame(AvatarFrameRequest request) throws IOException {
        AvatarFrame avatarFrame = avatarFrameMapper.toAvatarFrame(request);

        String url = fileService.uploadFile(request.getFile(), AVATAR_FRAME_FOLDER);
        avatarFrame.setImgSrc(url);

        return avatarFrameMapper.toAvatarFrameResponse(avatarFrameRepository.save(avatarFrame));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public AvatarFrameResponse updateAvatarFrame(Integer id, AvatarFrameRequest request) {
        AvatarFrame avatarFrame =
                avatarFrameRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.GENRE_NOT_EXISTED));

        avatarFrameMapper.updateAvatarFrame(avatarFrame, request);

        return avatarFrameMapper.toAvatarFrameResponse(avatarFrameRepository.save(avatarFrame));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteAvatarFrame(Integer id) {
        avatarFrameRepository.deleteById(id);
    }
}
