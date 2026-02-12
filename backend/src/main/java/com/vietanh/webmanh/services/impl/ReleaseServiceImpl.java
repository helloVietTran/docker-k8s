package com.vietanh.webmanh.services.impl;

import com.vietanh.webmanh.constants.ErrorCode;
import com.vietanh.webmanh.constants.ReleaseStatus;
import com.vietanh.webmanh.dbs.postgres.models.ReleaseCalendar;
import com.vietanh.webmanh.dbs.postgres.repositories.ReleaseCalendarRepository;
import com.vietanh.webmanh.dtos.requests.ReleaseUpdateRequest;
import com.vietanh.webmanh.dtos.responses.ReleaseResponse;
import com.vietanh.webmanh.exception.AppException;
import com.vietanh.webmanh.mappers.ReleaseCalendarMapper;
import com.vietanh.webmanh.services.ReleaseService;
import com.vietanh.webmanh.utils.AuthUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReleaseServiceImpl implements ReleaseService {
    ReleaseCalendarRepository releaseRepository;
    ReleaseCalendarMapper releaseMapper;

    @Override
    public ReleaseResponse getReleaseComicCalendar(
            Integer comicId
    ) {
        // Pageable sẽ giữ nguyên JPQL, khi covert thành SQL -> thêm LIMIT và OFFSET
        ReleaseCalendar rc = releaseRepository
                .findCurrentScheduledRelease(
                        comicId,
                        ReleaseStatus.SCHEDULED,
                        PageRequest.of(0, 1)
                )
                .stream()
                .findFirst()
                .orElseThrow( () -> new AppException(ErrorCode.RELEASE_SCHEDULE_NOT_FOUND));

        return releaseMapper.toReleaseResponse(rc);
    }

    @Override
    @Transactional
    public ReleaseResponse updateReleaseComicCalendar(
            Integer comicId,
            ReleaseUpdateRequest request
    ) {
        Integer userId = AuthUtil.getCurrentUserId();
        boolean isAdmin = AuthUtil.hasRole("ADMIN");

        ReleaseCalendar currentScheduled = releaseRepository
                .findCurrentScheduledRelease(
                        comicId,
                        ReleaseStatus.SCHEDULED,
                        PageRequest.of(0, 1)
                )
                .stream()
                .findFirst()
                .orElseThrow( () -> new AppException(ErrorCode.RELEASE_SCHEDULE_NOT_FOUND));

        if (!isAdmin && !currentScheduled.getAuthorId().equals(userId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        // Hủy lịch phát hành cũ
        currentScheduled.setReleaseStatus(ReleaseStatus.CANCELED);
        currentScheduled.setUpdatedBy(userId);
        currentScheduled.setUpdatedAt(Instant.now());
        releaseRepository.save(currentScheduled);

        // Lập lịch phát hành mới dựa trên lịch cũ
        ReleaseCalendar newSchedule = ReleaseCalendar.builder()
                .comicId(comicId)
                .authorId(currentScheduled.getAuthorId()) // OWNER = AUTHOR
                .releaseAt(request.getReleaseAt())
                .releaseStatus(ReleaseStatus.SCHEDULED)
                .updatedBy(userId)
                .build();

        releaseRepository.save(newSchedule);

        return releaseMapper.toReleaseResponse(newSchedule);
    }

    @Override
    @Transactional
    public ReleaseResponse cancelReleaseComicCalendar(Integer comicId) {

        Integer userId = AuthUtil.getCurrentUserId();
        boolean isAdmin = AuthUtil.hasRole("ADMIN");

        ReleaseCalendar scheduled = releaseRepository
                .findCurrentScheduledRelease(
                        comicId,
                        ReleaseStatus.SCHEDULED,
                        PageRequest.of(0, 1)
                )
                .stream()
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.RELEASE_SCHEDULE_NOT_FOUND));

        if (!isAdmin && !scheduled.getAuthorId().equals(userId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        scheduled.setReleaseStatus(ReleaseStatus.CANCELED);
        scheduled.setUpdatedBy(userId);
        scheduled.setUpdatedAt(Instant.now());

        releaseRepository.save(scheduled);

        return releaseMapper.toReleaseResponse(scheduled);
    }

}
