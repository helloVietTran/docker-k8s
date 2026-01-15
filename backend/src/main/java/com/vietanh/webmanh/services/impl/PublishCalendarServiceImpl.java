package com.vietanh.webmanh.services.impl;

import com.vietanh.webmanh.constants.PublishStatus;
import com.vietanh.webmanh.constants.PublishTargetType;
import com.vietanh.webmanh.dbs.postgres.models.PublishCalendar;
import com.vietanh.webmanh.dbs.postgres.repositories.PublishCalendarRepository;
import com.vietanh.webmanh.dtos.requests.UpdatePublishCalendarRequest;
import com.vietanh.webmanh.dtos.responses.PublishCalendarResponse;
import com.vietanh.webmanh.mappers.PublishCalendarMapper;
import com.vietanh.webmanh.services.PublishCalendarService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PublishCalendarServiceImpl implements PublishCalendarService {
    PublishCalendarRepository publishCalendarRepository;
    PublishCalendarMapper calendarMapper;

    public PublishCalendarResponse updateComicPublishCalendar(
            Integer comicId, UpdatePublishCalendarRequest request
    ) {
        List<PublishCalendar> actives =
                publishCalendarRepository.findActiveByTarget(
                        PublishTargetType.COMIC,
                        comicId,
                        PublishStatus.SCHEDULED
                );

        // Cancel lịch cũ
        for (PublishCalendar old : actives) {
            old.setPublishStatus(PublishStatus.CANCELED);
        }

        PublishCalendar calendar = PublishCalendar.builder()
                .publishTargetType(PublishTargetType.COMIC)
                .targetId(comicId)
                .publishAt(request.getPublishAt())
                .publishStatus(PublishStatus.SCHEDULED)
                .createdAt(Instant.now())
                .retryCount(0)
                .build();

        return calendarMapper.toPublishCalendarResponse(publishCalendarRepository.save(calendar));
    }

    @Override
    public List<PublishCalendarResponse> getComicPublishCalendar(Integer comicId) {
         return publishCalendarRepository.findByTarget(
                PublishTargetType.COMIC,
                comicId
         )
                 .stream()
                 .map(calendarMapper::toPublishCalendarResponse)
                 .toList();
    }
}
