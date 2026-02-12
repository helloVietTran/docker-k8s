package com.vietanh.webmanh.services.impl;

import com.vietanh.webmanh.constants.ErrorCode;
import com.vietanh.webmanh.dbs.postgres.models.FollowComic;
import com.vietanh.webmanh.dbs.postgres.repositories.ComicRepository;
import com.vietanh.webmanh.dbs.postgres.repositories.FollowComicRepository;
import com.vietanh.webmanh.dtos.responses.FollowComicResponse;
import com.vietanh.webmanh.exception.AppException;
import com.vietanh.webmanh.services.FollowService;
import com.vietanh.webmanh.utils.AuthUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FollowServiceImpl implements FollowService {
    FollowComicRepository followComicRepository;
    ComicRepository comicRepository;

    @Override
    @Transactional
    public void followComic(Integer comicId) {
        if (!comicRepository.existsById(comicId)) {
            throw new AppException(ErrorCode.COMIC_NOT_EXISTED);
        }

        Integer userId = AuthUtil.getCurrentUserId();

        if (followComicRepository.existsByUserIdAndComicId(userId, comicId)) {
            return;
        }

        FollowComic follow = FollowComic.builder()
                .userId(userId)
                .comicId(comicId)
                .followedAt(Instant.now())
                .priority(false)
                .notifyEnabled(true)
                .build();

        followComicRepository.save(follow);
    }

    @Override
    @Transactional
    public void unfollowComic(Integer comicId) {
        Integer userId = AuthUtil.getCurrentUserId();
        followComicRepository.deleteByUserIdAndComicId(userId, comicId);
    }

    @Override
    @Transactional
    public void togglePriority(Integer comicId) {
        Integer userId = AuthUtil.getCurrentUserId();
        FollowComic follow = followComicRepository.findByUserIdAndComicId(userId, comicId)
                .orElseThrow(() -> new AppException(ErrorCode.FOLLOW_NOT_FOUND));

        follow.setPriority(!follow.getPriority());
        followComicRepository.save(follow);
    }

    @Override
    public List<FollowComicResponse> getMyFollowedComics() {
        Integer userId = AuthUtil.getCurrentUserId();
        return followComicRepository.findAllByUserIdOrderByPriorityDescFollowedAtDesc(userId)
                .stream()
                .map(follow -> FollowComicResponse.builder()
                        .comicId(follow.getComicId())
                        .followedAt(follow.getFollowedAt())
                        .priority(follow.getPriority())
                        .notifyEnabled(follow.getNotifyEnabled())
                        .build())
                .toList();
    }

    @Override
    public void toggleNotify(Integer comicId) {
        Integer userId = AuthUtil.getCurrentUserId();

        FollowComic follow = followComicRepository.findByUserIdAndComicId(userId, comicId)
                .orElseThrow(() -> new AppException(ErrorCode.FOLLOW_NOT_FOUND));

        follow.setNotifyEnabled(!follow.getNotifyEnabled());

        followComicRepository.save(follow);
    }

    @Override
    public boolean isFollowed(Integer comicId) {
        Integer userId = AuthUtil.getCurrentUserId();
        return followComicRepository.existsByUserIdAndComicId(userId, comicId);
    }
}
