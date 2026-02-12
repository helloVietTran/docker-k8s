package com.vietanh.webmanh.services.impl;

import com.vietanh.webmanh.constants.ReviewStatus;
import com.vietanh.webmanh.constants.ErrorCode;
import com.vietanh.webmanh.dbs.postgres.models.Comic;
import com.vietanh.webmanh.dbs.postgres.models.ReviewLog;
import com.vietanh.webmanh.dbs.postgres.repositories.ComicRepository;
import com.vietanh.webmanh.dbs.postgres.repositories.ReleaseCalendarRepository;
import com.vietanh.webmanh.dbs.postgres.repositories.ReviewLogRepository;
import com.vietanh.webmanh.dtos.responses.ComicResponse;
import com.vietanh.webmanh.exception.AppException;
import com.vietanh.webmanh.mappers.ComicMapper;
import com.vietanh.webmanh.services.ReviewService;
import com.vietanh.webmanh.utils.AuthUtil;
import com.vietanh.webmanh.utils.PathUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewServiceImpl implements ReviewService {
    ComicRepository comicRepository;
    ReviewLogRepository reviewLogRepository;
    ReleaseCalendarRepository releaseCalendarRepository;

    ComicMapper comicMapper;

    @Override
    @Transactional
    public ComicResponse reviewComic(Integer comicId, ReviewStatus reviewStatus) {
        Integer adminId = AuthUtil.getCurrentUserId();
        Comic comic = comicRepository.findById(comicId)
                .orElseThrow(()-> new AppException(ErrorCode.COMIC_NOT_EXISTED));


        // ghi log
        ReviewLog reviewLog = ReviewLog.builder()
                .adminId(adminId)
                .comicId(comicId)
                .reviewStatus(reviewStatus)
                .build();

        reviewLogRepository.save(reviewLog);

        comic.setReviewStatus(reviewStatus);
        Comic savedComic = comicRepository.save(comic);

        // mapping response
        ComicResponse response = comicMapper.toComicResponse(savedComic);
        if (savedComic.getCoverSrc() != null) {
            response.setCoverSrc(
                    savedComic.getCoverSrc().stream()
                            .map(PathUtil::toUrlPath)
                            .toList()
            );
        }
        response.setAuthorName(comic.getAuthor().getUsername());
        return response;
    }
}
