package com.viettran.reading_story_web.scheduler;

import java.time.Instant;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.viettran.reading_story_web.annotation.RedisDistributedLock;
import com.viettran.reading_story_web.entity.mysql.Inventory;
import com.viettran.reading_story_web.repository.jpa.InventoryRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InventoryJobScheduler {
    InventoryRepository inventoryRepository;

    @Scheduled(cron = "0 0 2 * * *") // xóa item hết hạn (lúc 2h sáng)
    @RedisDistributedLock(key = "job:inventory:clean:expired", timeout = 600)
    public void cleanExpiredInventories() {
        log.info("[InventoryJobScheduler] Cleaning expired inventories...");
        List<Inventory> expiredInventories = inventoryRepository.findByExpirationDateBefore(Instant.now());
        if (!expiredInventories.isEmpty()) {
            inventoryRepository.deleteAll(expiredInventories);
            log.info("[InventoryJobScheduler] Deleted {} expired inventory items", expiredInventories.size());
        } else {
            log.debug("[InventoryJobScheduler] No expired inventory items found");
        }
    }
}
