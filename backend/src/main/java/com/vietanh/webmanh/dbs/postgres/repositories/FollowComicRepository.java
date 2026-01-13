package com.vietanh.webmanh.dbs.postgres.repositories;

import com.vietanh.webmanh.dbs.postgres.models.FollowComic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowComicRepository extends JpaRepository<FollowComic, Integer> {
    Optional<FollowComic> findByUserIdAndComicId(Integer userId, Integer comicId);
    List<FollowComic> findAllByUserIdOrderByPriorityDescFollowedAtDesc(Integer userId);
    void deleteByUserIdAndComicId(Integer userId, Integer comicId);
    boolean existsByUserIdAndComicId(Integer userId, Integer comicId);
}
