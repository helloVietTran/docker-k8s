package com.vietanh.webmanh.dbs.postgres.repositories;

import com.vietanh.webmanh.constants.ReviewStatus;
import com.vietanh.webmanh.dbs.postgres.models.Comic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface ComicRepository extends JpaRepository<Comic, Integer>,
        JpaSpecificationExecutor<Comic> {

    Optional<Comic> findByComicIdAndReviewStatusIn(
            Integer id,
            Collection<ReviewStatus> decisions
    );

}
