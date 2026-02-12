package com.vietanh.webmanh.mappers;

import com.vietanh.webmanh.dbs.postgres.models.ReleaseCalendar;
import com.vietanh.webmanh.dtos.responses.ReleaseResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReleaseCalendarMapper {

    ReleaseResponse toReleaseResponse(ReleaseCalendar calendar);
}
