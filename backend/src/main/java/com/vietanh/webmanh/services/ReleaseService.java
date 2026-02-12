package com.vietanh.webmanh.services;

import com.vietanh.webmanh.constants.ReleaseStatus;
import com.vietanh.webmanh.dtos.requests.ReleaseUpdateRequest;
import com.vietanh.webmanh.dtos.responses.ReleaseResponse;

import java.util.List;

public interface ReleaseService {
    ReleaseResponse getReleaseComicCalendar(
            Integer comicId
    );

    ReleaseResponse updateReleaseComicCalendar(
            Integer comicId, ReleaseUpdateRequest request
    );

    ReleaseResponse cancelReleaseComicCalendar(Integer comicId);
}
