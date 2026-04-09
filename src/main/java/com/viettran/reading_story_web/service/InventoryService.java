package com.viettran.reading_story_web.service;

import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.viettran.reading_story_web.dto.response.InventoryResponse;
import com.viettran.reading_story_web.entity.mysql.Inventory;
import com.viettran.reading_story_web.exception.AppException;
import com.viettran.reading_story_web.exception.ErrorCode;
import com.viettran.reading_story_web.mapper.AvatarFrameMapper;
import com.viettran.reading_story_web.repository.jpa.InventoryRepository;
import com.viettran.reading_story_web.utils.DateTimeFormatUtil;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InventoryService {
    InventoryRepository inventoryRepository;

    AvatarFrameMapper avatarFrameMapper;

    DateTimeFormatUtil dateTimeFormatUtil;
    AuthenticationService authenticationService;

    public InventoryResponse getUserInventory() {
        String userId = authenticationService.getCurrentUserId();
        // tìm bản ghi còn hạn sử dụng
        Optional<Inventory> userInventoryOptional =
                inventoryRepository.findFirstByUserIdAndExpirationDateAfter(userId, Instant.now());

        if (userInventoryOptional.isEmpty()) throw new AppException(ErrorCode.USER_INVENTORY_NOT_EXISTED);

        Inventory inventory = userInventoryOptional.get();

        return InventoryResponse.builder()
                .id(inventory.getId())
                .avatarFrame(avatarFrameMapper.toAvatarFrameResponse(inventory.getAvatarFrame()))
                .createdAt(dateTimeFormatUtil.format(inventory.getCreatedDate()))
                .build();
    }
}
