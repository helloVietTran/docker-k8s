package com.vietanh.webmanh.mappers;

import com.vietanh.webmanh.dbs.postgres.models.PublishCalendar;
import com.vietanh.webmanh.dtos.responses.PublishCalendarResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PublishCalendarMapper {

    PublishCalendarResponse toPublishCalendarResponse(PublishCalendar calendar);
}
