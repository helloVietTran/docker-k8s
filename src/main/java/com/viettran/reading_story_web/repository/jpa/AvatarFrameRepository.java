package com.viettran.reading_story_web.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.viettran.reading_story_web.entity.mysql.AvatarFrame;

@Repository
public interface AvatarFrameRepository extends JpaRepository<AvatarFrame, Integer> {}
