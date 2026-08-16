package com.viettran.reading_story_web.repository.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.viettran.reading_story_web.entity.redis.StoryCache;

@Repository
public interface StoryCacheRepository extends CrudRepository<StoryCache, Integer> {}
