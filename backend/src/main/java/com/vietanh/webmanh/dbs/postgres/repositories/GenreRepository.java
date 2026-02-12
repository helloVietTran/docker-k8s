package com.vietanh.webmanh.dbs.postgres.repositories;

import com.vietanh.webmanh.dbs.postgres.models.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Integer> {
    Set<Genre> findByCodeIn(Collection<Integer> codes);
}
