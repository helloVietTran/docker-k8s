package com.viettran.reading_story_web.service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.viettran.reading_story_web.dto.request.CommentRequest;
import com.viettran.reading_story_web.dto.request.CommentUpdationRequest;
import com.viettran.reading_story_web.dto.response.CommentResponse;
import com.viettran.reading_story_web.dto.response.PageResponse;
import com.viettran.reading_story_web.dto.response.StoryResponse;
import com.viettran.reading_story_web.dto.response.UserResponse;
import com.viettran.reading_story_web.entity.mysql.*;
import com.viettran.reading_story_web.exception.AppException;
import com.viettran.reading_story_web.exception.ErrorCode;
import com.viettran.reading_story_web.mapper.CommentMapper;
import com.viettran.reading_story_web.mapper.LevelMapper;
import com.viettran.reading_story_web.repository.jpa.*;
import com.viettran.reading_story_web.utils.DateTimeFormatUtil;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentService {
    StoryRepository storyRepository;
    CommentRepository commentRepository;
    UserRepository userRepository;
    ChapterRepository chapterRepository;
    InventoryRepository inventoryRepository;
    AuthenticationService authenticationService;

    DateTimeFormatUtil dateTimeFormatUtil;

    CommentMapper commentMapper;
    LevelMapper levelMapper;

    @Transactional
    public CommentResponse createComment(CommentRequest request) {
        String userId = authenticationService.getCurrentUserId();
        Story story = storyRepository
                .findById(request.getStoryId())
                .orElseThrow(() -> new AppException(ErrorCode.STORY_NOT_EXISTED));

        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Chapter chapter = chapterRepository
                .findByStoryIdAndChap(request.getStoryId(), request.getAtChapter())
                .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_EXISTED));

        Comment comment = commentMapper.toComment(request);

        comment.setUser(user);
        comment.setStory(story);
        comment.setChapter(chapter);

        comment.setCreatedAt(Instant.now());
        comment.setUpdatedAt(Instant.now());

        if (request.getParentCommentId() == null) {
            int maxRight =
                    commentRepository.findMaxRightByChapterId(chapter.getId()).orElse(0);

            comment.setLeftVal(maxRight + 1);
            comment.setRightVal(maxRight + 2);

        } else {
            Comment parent = commentRepository
                    .findById(request.getParentCommentId())
                    .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_EXISTED));

            int right = parent.getRightVal();

            commentRepository.shiftRightFrom(chapter.getId(), right);
            commentRepository.shiftLeftFrom(chapter.getId(), right);

            comment.setLeftVal(right);
            comment.setRightVal(right + 1);
        }

        story.setCommentCount(story.getCommentCount() + 1);

        commentRepository.save(comment);
        storyRepository.save(story);

        return commentMapper.toCommentResponse(comment);
    }

    @PreAuthorize("#id == authentication.name")
    public CommentResponse updateComment(CommentUpdationRequest request, String commentId, String id) {
        Optional<Comment> commentOptional = commentRepository.findByIdAndUserId(commentId, id);
        if (commentOptional.isEmpty()) throw new AppException(ErrorCode.COMMENT_NOT_EXISTED);

        commentMapper.updateComment(commentOptional.get(), request);

        return commentMapper.toCommentResponse(commentRepository.save(commentOptional.get()));
    }

    @PreAuthorize("#id == authentication.name")
    @Transactional
    public void deleteComment(String commentId) {

        Comment comment = commentRepository
                .findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_EXISTED));

        int left = comment.getLeftVal();
        int right = comment.getRightVal();
        int width = right - left + 1;

        String chapterId = comment.getChapter().getId();

        commentRepository.deleteSubtree(chapterId, left, right);

        commentRepository.shiftLeftAfterDelete(chapterId, right, width);
        commentRepository.shiftRightAfterDelete(chapterId, right, width);
    }

    public List<CommentResponse> getCommentsTreeByChapterId(String chapterId) {

        List<Comment> comments = commentRepository.findAllByChapterOrderByLeft(chapterId);

        List<String> userIds =
                comments.stream().map(c -> c.getUser().getId()).distinct().toList();

        List<User> users = userRepository.findUsersWithLevel(userIds);
        Map<String, User> userMap = users.stream().collect(Collectors.toMap(User::getId, u -> u));

        List<Inventory> inventories = inventoryRepository.findActiveAvatarFrames(userIds);

        Map<String, String> avatarFrameMap = new HashMap<>();
        for (Inventory inv : inventories) {
            avatarFrameMap.put(inv.getUser().getId(), inv.getAvatarFrame().getImgSrc());
        }

        return comments.stream()
                .map(comment -> {
                    CommentResponse res = commentMapper.toCommentResponse(comment);

                    res.setCreatedAt(dateTimeFormatUtil.format(comment.getCreatedAt()));
                    res.setUpdatedAt(dateTimeFormatUtil.format(comment.getUpdatedAt()));

                    res.setLeftVal(comment.getLeftVal());
                    res.setRightVal(comment.getRightVal());

                    User user = userMap.get(comment.getUser().getId());

                    res.setUser(UserResponse.builder()
                            .id(user.getId())
                            .name(user.getName())
                            .imgSrc(user.getImgSrc())
                            .frame(avatarFrameMap.getOrDefault(user.getId(), ""))
                            .level(levelMapper.toLevelResponse(user.getLevel()))
                            .build());

                    return res;
                })
                .toList();
    }

    public List<CommentResponse> getCommentsTreeByStoryId(Integer storyId) {

        List<Comment> comments = commentRepository.findAllByStoryOrderByLeft(storyId);

        List<String> userIds =
                comments.stream().map(c -> c.getUser().getId()).distinct().toList();

        List<User> users = userRepository.findUsersWithLevel(userIds);
        Map<String, User> userMap = users.stream().collect(Collectors.toMap(User::getId, u -> u));

        List<Inventory> inventories = inventoryRepository.findActiveAvatarFrames(userIds);

        Map<String, String> avatarFrameMap = new HashMap<>();
        for (Inventory inv : inventories) {
            avatarFrameMap.put(inv.getUser().getId(), inv.getAvatarFrame().getImgSrc());
        }

        return comments.stream()
                .map(comment -> {
                    CommentResponse res = commentMapper.toCommentResponse(comment);

                    res.setCreatedAt(dateTimeFormatUtil.format(comment.getCreatedAt()));
                    res.setUpdatedAt(dateTimeFormatUtil.format(comment.getUpdatedAt()));

                    res.setLeftVal(comment.getLeftVal());
                    res.setRightVal(comment.getRightVal());

                    User user = userMap.get(comment.getUser().getId());

                    res.setUser(UserResponse.builder()
                            .id(user.getId())
                            .name(user.getName())
                            .imgSrc(user.getImgSrc())
                            .frame(avatarFrameMap.getOrDefault(user.getId(), ""))
                            .level(levelMapper.toLevelResponse(user.getLevel()))
                            .build());

                    return res;
                })
                .toList();
    }

    // ================= PAGE BUILDER =================
    private PageResponse<CommentResponse> buildPageResponse(
            int page, Page<Comment> rootPage, List<CommentResponse> data) {

        return PageResponse.<CommentResponse>builder()
                .currentPage(page)
                .pageSize(rootPage.getSize())
                .totalElements(rootPage.getTotalElements()) // chỉ count root
                .totalPages(rootPage.getTotalPages())
                .data(data)
                .build();
    }

    public PageResponse<CommentResponse> getNewComments() {
        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(0, 10, sort);

        var pageData = commentRepository.findAll(pageable);

        List<CommentResponse> commentResponses = pageData.getContent().stream()
                .map(comment -> {
                    CommentResponse commentResponse = commentMapper.toCommentResponse(comment);
                    commentResponse.setStory(StoryResponse.builder()
                            .name(comment.getStory().getName())
                            .id(comment.getStory().getId())
                            .newestChapter(comment.getStory().getNewestChapter())
                            .viewCount(comment.getStory().getViewCount())
                            .imgSrc(comment.getStory().getImgSrc())
                            .slug(comment.getStory().getSlug())
                            .build());

                    commentResponse.setUser(UserResponse.builder()
                            .id(comment.getUser().getId())
                            .name(comment.getUser().getName())
                            .imgSrc(comment.getUser().getImgSrc())
                            .build());

                    commentResponse.setCreatedAt(dateTimeFormatUtil.format(comment.getCreatedAt()));
                    commentResponse.setUpdatedAt(dateTimeFormatUtil.format(comment.getUpdatedAt()));
                    return commentResponse;
                })
                .toList();

        return PageResponse.<CommentResponse>builder()
                .currentPage(0)
                .pageSize(pageData.getSize())
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .data(commentResponses)
                .build();
    }

    public PageResponse<CommentResponse> getMyComment() {
        String userId = authenticationService.getCurrentUserId();

        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(0, 10, sort);
        var pageData = commentRepository.findAllByUserId(pageable, userId);

        return PageResponse.<CommentResponse>builder()
                .currentPage(0)
                .pageSize(pageData.getSize())
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .data(pageData.getContent().stream()
                        .map(comment -> {
                            CommentResponse commentResponse = commentMapper.toCommentResponse(comment);
                            commentResponse.setCreatedAt(dateTimeFormatUtil.format(comment.getCreatedAt()));
                            commentResponse.setStory(StoryResponse.builder()
                                    .imgSrc(comment.getStory().getImgSrc())
                                    .name(comment.getStory().getName())
                                    .build());

                            return commentResponse;
                        })
                        .toList())
                .build();
    }

    public PageResponse<CommentResponse> getCommentsByUserId(String userId, int page, int size) {
        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        var pageData = commentRepository.findAllByUserId(pageable, userId);

        List<CommentResponse> commentResponses = pageData.getContent().stream()
                .map(comment -> {
                    CommentResponse commentResponse = commentMapper.toCommentResponse(comment);

                    commentResponse.setStory(StoryResponse.builder()
                            .name(comment.getStory().getName())
                            .id(comment.getStory().getId())
                            .newestChapter(comment.getStory().getNewestChapter())
                            .viewCount(comment.getStory().getViewCount())
                            .imgSrc(comment.getStory().getImgSrc())
                            .slug(comment.getStory().getSlug())
                            .build());

                    return commentResponse;
                })
                .toList();

        return buildPageResponse(page, pageData, commentResponses);
    }
}
