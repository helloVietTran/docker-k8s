package com.viettran.reading_story_web.scheduler;

import java.time.Instant;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.viettran.reading_story_web.entity.mysql.Inventory;
import com.viettran.reading_story_web.repository.jpa.InventoryRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InventoryJobScheduler {
    InventoryRepository inventoryRepository;

    @Scheduled(cron = "0 0 2 * * *") // xóa item hết hạn
    public void cleanExpiredInventories() {
        List<Inventory> expiredInventories = inventoryRepository.findByExpirationDateBefore(Instant.now());
        if (!expiredInventories.isEmpty()) {
            inventoryRepository.deleteAll(expiredInventories);
        }
    }
}
