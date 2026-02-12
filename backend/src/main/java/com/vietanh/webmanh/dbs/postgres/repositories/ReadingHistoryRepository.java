package com.vietanh.webmanh.dbs.postgres.repositories;

import com.vietanh.webmanh.dbs.postgres.models.ReadingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReadingHistoryRepository extends JpaRepository<ReadingHistory, Integer> {
    List<ReadingHistory> findAllByUserId(Integer userId);

    Optional<ReadingHistory> findByUserIdAndComicId(
            Integer userId,
            Integer comicId
    );

    void deleteByUserIdAndComicId(Integer userId, Integer comicId);
}
