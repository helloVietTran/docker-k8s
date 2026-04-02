package com.viettran.reading_story_web.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.viettran.reading_story_web.dto.request.ReadingHistoryRequest;
import com.viettran.reading_story_web.dto.response.ReadingHistoryResponse;
import com.viettran.reading_story_web.dto.response.StoryResponse;
import com.viettran.reading_story_web.entity.mysql.ReadingHistory;
import com.viettran.reading_story_web.entity.mysql.Story;
import com.viettran.reading_story_web.entity.mysql.User;
import com.viettran.reading_story_web.exception.AppException;
import com.viettran.reading_story_web.exception.ErrorCode;
import com.viettran.reading_story_web.mapper.ReadingHistoryMapper;
import com.viettran.reading_story_web.repository.jpa.ReadingHistoryRepository;
import com.viettran.reading_story_web.repository.jpa.StoryRepository;
import com.viettran.reading_story_web.repository.jpa.UserRepository;
import com.viettran.reading_story_web.utils.DateTimeFormatUtil;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReadingHistoryService {
    ReadingHistoryRepository readingHistoryRepository;
    UserRepository userRepository;
    StoryRepository storyRepository;

    ReadingHistoryMapper readingHistoryMapper;

    AuthenticationService authenticationService;
    DateTimeFormatUtil dateTimeFormatUtil;

    public List<ReadingHistoryResponse> getReadingHistory() {
        String userId = authenticationService.getCurrentUserId();
        List<ReadingHistory> readingHistories = readingHistoryRepository.findByUserId(userId);

        return readingHistories.stream()
                .map(readingHistory -> {
                    ReadingHistoryResponse response = readingHistoryMapper.toReadingHistoryResponse(readingHistory);
                    Story story = readingHistory.getStory();
                    response.setStory(StoryResponse.builder()
                            .name(story.getName())
                            .id(story.getId())
                            .newestChapter(story.getNewestChapter())
                            .updatedAt(dateTimeFormatUtil.format(story.getUpdatedAt()))
                            .slug(story.getSlug())
                            .imgSrc(story.getImgSrc())
                            .build());
                    return response;
                })
                .toList();
    }

    public ReadingHistoryResponse updateReadingHistory(ReadingHistoryRequest request) {
        String userId = authenticationService.getCurrentUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        Story story = storyRepository
                .findById(request.getStoryId())
                .orElseThrow(() -> new AppException(ErrorCode.STORY_NOT_EXISTED));

        // Tìm lịch sử đọc truyện theo userId và storyId
        Optional<ReadingHistory> optionalHistory =
                readingHistoryRepository.findByUserIdAndStoryId(userId, request.getStoryId());

        ReadingHistory history;

        if (optionalHistory.isPresent()) {
            history = optionalHistory.get();

            List<String> chaptersRead = history.getChaptersRead();

            // kiểm tra nếu chaptersRead không chứa phần tử thì add thêm vào lịch sử
            if (!chaptersRead.contains(request.getChapterRead().toString())) {
                chaptersRead.add(request.getChapterRead().toString());
            }

        } else history = createReadingHistory(user, story, request);

        readingHistoryRepository.save(history);
        return readingHistoryMapper.toReadingHistoryResponse(history);
    }

    public void deleteReadingHistory(String readingHistoryId) {
        readingHistoryRepository.deleteById(readingHistoryId);
    }

    private ReadingHistory createReadingHistory(User user, Story story, ReadingHistoryRequest request) {

        return ReadingHistory.builder()
                .story(story)
                .user(user)
                .chaptersRead(List.of(request.getChapterRead().toString()))
                .build();
    }
}
