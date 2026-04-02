package com.viettran.reading_story_web.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import org.springframework.stereotype.Service;

import com.viettran.reading_story_web.dto.request.BuyItemsRequest;
import com.viettran.reading_story_web.dto.response.InventoryResponse;
import com.viettran.reading_story_web.dto.response.PointResponse;
import com.viettran.reading_story_web.entity.mysql.*;
import com.viettran.reading_story_web.enums.PointHistoryType;
import com.viettran.reading_story_web.exception.AppException;
import com.viettran.reading_story_web.exception.ErrorCode;
import com.viettran.reading_story_web.mapper.AvatarFrameMapper;
import com.viettran.reading_story_web.mapper.PointMapper;
import com.viettran.reading_story_web.mapper.UserMapper;
import com.viettran.reading_story_web.repository.jpa.*;
import com.viettran.reading_story_web.utils.DateTimeFormatUtil;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PointService {
    PointRepository pointRepository;
    UserRepository userRepository;
    PointHistoryRepository pointHistoryRepository;
    AvatarFrameRepository avatarFrameRepository;
    InventoryRepository inventoryRepository;

    PointMapper pointMapper;
    AvatarFrameMapper avatarFrameMapper;
    UserMapper userMapper;

    AuthenticationService authenticationService;
    DateTimeFormatUtil dateTimeFormatUtil;

    public Point createPoint(String userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Point newPoint = Point.builder()
                .user(user)
                .total(0)
                .consecutiveAttendanceCount(0)
                .lastAttendanceDate(null)
                .build();

        return pointRepository.save(newPoint);
    }

    public void attendance() {
        String userId = authenticationService.getCurrentUserId();

        Point point =
                pointRepository.findByUserId(userId).orElseThrow(() -> new AppException(ErrorCode.POINT_NOT_EXISTED));

        Instant now = Instant.now();
        LocalDate today = now.atZone(ZoneId.systemDefault()).toLocalDate();

        // Lấy ngày từ lastAttendanceDate nếu tồn tại
        LocalDate lastAttendanceDate = point.getLastAttendanceDate() != null
                ? point.getLastAttendanceDate().atZone(ZoneId.systemDefault()).toLocalDate()
                : null;

        // Kiểm tra nếu đã điểm danh trong ngày hiện tại
        if (lastAttendanceDate != null && lastAttendanceDate.isEqual(today)) {
            throw new AppException(ErrorCode.ALREADY_ATTENDED_TODAY);
        }

        // Cập nhật thông tin điểm danh
        point.setLastAttendanceDate(now);
        point.setConsecutiveAttendanceCount(point.getConsecutiveAttendanceCount() + 1);

        int pointIncrease = 100;

        // Kiểm tra điểm danh liên tục 7 ngày, thưởng thêm 200 điểm
        if (point.getConsecutiveAttendanceCount() == 7) {
            pointIncrease += 200;
            point.setConsecutiveAttendanceCount(0);
        }

        point.setTotal(point.getTotal() + pointIncrease);

        // Thêm lịch sử
        PointHistory pointHistory = PointHistory.builder()
                .point(point)
                .type(PointHistoryType.ATTENDANCE)
                .pointFluctuation(pointIncrease)
                .description("+100")
                .build();
        pointHistory.setCreatedAt(now);

        pointRepository.save(point);
        pointHistoryRepository.save(pointHistory);
    }

    public PointResponse getPoint() {
        String userId = authenticationService.getCurrentUserId();

        Point point = pointRepository.findByUserId(userId).orElseGet(() -> createPoint(userId));
        ;

        return pointMapper.toPointResponse(point);
    }

    public InventoryResponse buyItem(BuyItemsRequest request) {
        User user = userRepository
                .findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Point point = pointRepository
                .findByUserId(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.POINT_NOT_EXISTED));

        AvatarFrame avatarFrame = avatarFrameRepository
                .findById(request.getItemId())
                .orElseThrow(() -> new AppException(ErrorCode.AVATAR_FRAME_NOT_EXISTED));
        // kiểm tra đã mua khung chưa
        if (inventoryRepository.existsByUserIdAndExpirationDateAfter(request.getUserId(), Instant.now()))
            throw new AppException(ErrorCode.BOUGHT_AVATAR_FRAME);

        if (point.getTotal() < avatarFrame.getPrice()) throw new AppException(ErrorCode.NOT_ENOUGH_POINT);
        point.setTotal(point.getTotal() - avatarFrame.getPrice());

        Inventory inventory = new Inventory(user, avatarFrame, 86400);

        // thêm lịch sử
        PointHistory pointHistory = PointHistory.builder()
                .point(point)
                .type(PointHistoryType.BUY_ITEMS)
                .pointFluctuation(100)
                .description("-100")
                .build();
        pointHistory.setCreatedAt(Instant.now());

        pointHistoryRepository.save(pointHistory);
        pointRepository.save(point);
        userRepository.save(user);
        inventoryRepository.save(inventory);

        return InventoryResponse.builder()
                .avatarFrame(avatarFrameMapper.toAvatarFrameResponse(avatarFrame))
                .user(userMapper.toUserResponse(user))
                .createdAt(dateTimeFormatUtil.format(inventory.getCreatedDate()))
                .expirationDate(dateTimeFormatUtil.format(inventory.getExpirationDate()))
                .build();
    }
}
