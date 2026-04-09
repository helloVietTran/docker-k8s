package com.viettran.reading_story_web.utils;

import java.io.IOException;
import java.util.List;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettran.reading_story_web.exception.AppException;
import com.viettran.reading_story_web.exception.ErrorCode;

@Converter
public class ListToJsonConverterUtil implements AttributeConverter<List<String>, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        try {
            // Chuyển thành chuỗi JSON trước khi lưu db
            return objectMapper.writeValueAsString(attribute);
        } catch (Exception e) {

            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        try {
            // Chuyển chuỗi JSON thành List<String>
            return objectMapper.readValue(dbData, List.class);
        } catch (IOException e) {

            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }
}
