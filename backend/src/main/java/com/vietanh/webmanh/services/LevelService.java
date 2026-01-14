package com.vietanh.webmanh.services;

import com.vietanh.webmanh.dbs.postgres.models.Level;

public interface LevelService {
    Level getOrCreateLevel(Integer userId);
}
