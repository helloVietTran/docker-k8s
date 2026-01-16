package com.vietanh.webmanh.constants;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(1999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_SIGNATURE(1002, "Invalid token signature", HttpStatus.UNAUTHORIZED),

    TOKEN_EXPIRED(1003, "Token expired", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_EXPIRED(1004, "Refresh token expired", HttpStatus.UNAUTHORIZED),

    TOKEN_REVOKED(1005, "Token has been revoked", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALIDATED(1006, "Token invalidated due to credential change", HttpStatus.UNAUTHORIZED),

    AUTH_SERVICE_UNAVAILABLE(1500, "Authentication service unavailable", HttpStatus.SERVICE_UNAVAILABLE),

    PASSWORDS_DO_NOT_MATCH(41, "Password and confirmPassword not match", HttpStatus.BAD_REQUEST),
    SEND_WELCOME_MAIL_FAILED(81, "Send welcome mail failed", HttpStatus.BAD_REQUEST),

    ERROR_REPORTER_NOT_EXISTED(42, "This property is not allow null", HttpStatus.BAD_REQUEST),
    ERROR_ALREADY_REPORTED(43, "Error already report", HttpStatus.BAD_REQUEST),

    STORY_STATUS_ERROR(92, "Story status must different APPROVE_PENDING", HttpStatus.BAD_REQUEST),

    REQUIRED_IMAGE(51, "Image is required", HttpStatus.BAD_REQUEST),

    FILE_STORAGE_ERROR(55, "Storage file error", HttpStatus.INTERNAL_SERVER_ERROR),
    DELETED_FILE_ERROR(161, "Delete file on disk failed", HttpStatus.INTERNAL_SERVER_ERROR),

    FOLLOW_NOT_FOUND(181, "Follow comic record not found", HttpStatus.NOT_FOUND),

    ALREADY_READING_BOOK(305, "You already read this chapter", HttpStatus.BAD_REQUEST),

    INVALID_REFRESH_TOKEN(1007, "Invalid refresh token", HttpStatus.UNAUTHORIZED),

    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL(1002, "Invalid email", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1003, "Length of password must be between 6 and 20 characters", HttpStatus.BAD_REQUEST),
    INVALID_CONFIRM_PASSWORD(1007, "Confirm password not match", HttpStatus.BAD_REQUEST),
    INVALID_TOKEN(1004, "Invalid token or expired token", HttpStatus.BAD_REQUEST),
    RATING_POINT_INVALID(1005, "Rating point must be between 1 and 5 point", HttpStatus.BAD_REQUEST),
    USERNAME_IS_REQUIRED(1006, "User name is required", HttpStatus.BAD_REQUEST),

    BOUGHT_AVATAR_FRAME(1021, "You have bought avatar frame", HttpStatus.BAD_REQUEST),
    NOT_ENOUGH_TIME_REQUIRED(1007, "Please wait a few minutes before requesting a new token", HttpStatus.BAD_REQUEST),
    ALREADY_ATTENDED_TODAY(1041, "You have attended today", HttpStatus.BAD_REQUEST),

    USER_EXISTED(1010, "User existed", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1011, "User not existed", HttpStatus.NOT_FOUND),
    AVATAR_FRAME_NOT_EXISTED(1020, "Avatar frame not existed", HttpStatus.BAD_REQUEST),
    GENRE_NOT_EXISTED(1030, "Genre not existed", HttpStatus.NOT_FOUND),
    POINT_NOT_EXISTED(1040, "Point record not existed", HttpStatus.NOT_FOUND),
    COMIC_NOT_EXISTED(1050, "Story not existed", HttpStatus.NOT_FOUND),
    CHAPTER_NOT_EXISTED(1060, "Chapter not existed", HttpStatus.NOT_FOUND),
    LEVEL_NOT_EXISTED(1070, "Level record not existed", HttpStatus.NOT_FOUND),
    TOKEN_NOT_EXISTED(1080, "Token not existed", HttpStatus.BAD_REQUEST),
    COMMENT_NOT_EXISTED(1090, "Comment not existed", HttpStatus.NOT_FOUND),
    ROLE_NOT_EXISTED(1100, "Role not existed", HttpStatus.NOT_FOUND),
    PERMISSION_NOT_EXISTED(1110, "Permission not existed", HttpStatus.NOT_FOUND),
    USER_INVENTORY_NOT_EXISTED(1120, "User inventory not existed", HttpStatus.NOT_FOUND),
    FOLLOWED_STORY(1130, "You have followed this story", HttpStatus.BAD_REQUEST),

    SOME_FIELDS_REQUIRED(1300, "Some field required", HttpStatus.BAD_REQUEST),

    NO_FILE_UPLOADED(1200, "You must upload some file", HttpStatus.BAD_REQUEST),
    FAILED_UPLOAD_FILE(1201, "Failed to upload", HttpStatus.INTERNAL_SERVER_ERROR),
    NOT_IMAGE_FILE_TYPE(1202, "Please upload image type", HttpStatus.BAD_REQUEST),

    NOT_ENOUGH_POINT(1042, "You don't have enough point to buy it", HttpStatus.BAD_REQUEST),

    CANNOT_SEND_EMAIL(1990, "Can't not send email", HttpStatus.INTERNAL_SERVER_ERROR),

    UNAUTHENTICATED(1900, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1901, "You do not have permission", HttpStatus.FORBIDDEN),
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
