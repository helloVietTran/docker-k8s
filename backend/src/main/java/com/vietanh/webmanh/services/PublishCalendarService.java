package com.vietanh.webmanh.services;

import com.vietanh.webmanh.dbs.postgres.models.PublishCalendar;
import com.vietanh.webmanh.dtos.requests.UpdatePublishCalendarRequest;
import com.vietanh.webmanh.dtos.responses.PublishCalendarResponse;

import java.util.List;

public interface PublishCalendarService {
    PublishCalendarResponse updateComicPublishCalendar(
            Integer comicId, UpdatePublishCalendarRequest request
    );

    List<PublishCalendarResponse> getComicPublishCalendar(Integer comicId);
}
