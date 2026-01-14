package com.vietanh.webmanh.dbs.postgres.repositories;

import com.vietanh.webmanh.dbs.postgres.models.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LevelRepository extends JpaRepository<Level, Integer> {
    Optional<Level> findByUserId(Integer userId);
}
