package com.viettran.reading_story_web.repository.jpa;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;

import org.springframework.stereotype.Repository;

import com.viettran.reading_story_web.entity.mysql.Genre;
import com.viettran.reading_story_web.entity.mysql.Story;
import com.viettran.reading_story_web.enums.Gender;
import com.viettran.reading_story_web.enums.StoryStatus;

@Repository
public class CustomStoryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Story> findStories(Integer genreCode, Integer status, Integer sort, String keyword) {
        return findStoriesAdvanced(
                genreCode != null ? List.of(genreCode) : null, null, status, sort, null, null, keyword);
    }

    public List<Story> findStoriesAdvanced(
            List<Integer> genreCodes,
            List<Integer> notGenreCodes,
            Integer status,
            Integer sort,
            Integer minChapter,
            Integer gender,
            String keyword) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Story> query = cb.createQuery(Story.class);
        Root<Story> root = query.from(Story.class);
        Join<Story, Genre> genreJoin = root.join("genres", JoinType.LEFT);

        List<Predicate> predicates = buildPredicates(
                cb, query, root, genreJoin, genreCodes, notGenreCodes, status, minChapter, gender, keyword);

        query.select(root).where(predicates.toArray(new Predicate[0]));
        applySorting(cb, query, root, sort);

        return entityManager.createQuery(query).getResultList();
    }

    private List<Predicate> buildPredicates(
            CriteriaBuilder cb,
            CriteriaQuery<?> query,
            Root<Story> root,
            Join<Story, Genre> genreJoin,
            List<Integer> genreCodes,
            List<Integer> notGenreCodes,
            Integer status,
            Integer minChapter,
            Integer gender,
            String keyword) {

        List<Predicate> predicates = new ArrayList<>();

        // Lọc theo genreCodes
        if (genreCodes != null && !genreCodes.isEmpty()) {
            predicates.add(genreJoin.get("id").in(genreCodes));
        }

        // Lọc loại trừ notGenreCodes
        if (notGenreCodes != null && !notGenreCodes.isEmpty()) {
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<Story> subRoot = subquery.from(Story.class);
            Join<Story, Genre> subGenreJoin = subRoot.join("genres", JoinType.LEFT);

            subquery.select(subRoot.get("id")).where(subGenreJoin.get("id").in(notGenreCodes));
            predicates.add(cb.not(root.get("id").in(subquery)));
        }

        // Lọc theo status
        if (status != null) {
            predicates.add(cb.equal(root.get("status"), status == 1 ? StoryStatus.COMPLETED : StoryStatus.IN_PROCESS));
        }

        // Lọc theo gender
        if (gender != null && gender != -1) {
            if (gender == 2) {
                predicates.add(cb.notEqual(root.get("gender"), Gender.FEMALE)); // tìm không là male
            } else if (gender == 1) {
                predicates.add(cb.notEqual(root.get("gender"), Gender.MALE));
            }
        }

        // Lọc theo số chapter
        if (minChapter != null) {
            predicates.add(cb.greaterThan(root.get("newestChapter"), minChapter));
        }

        // loc theo keyword
        if (keyword != null && !keyword.trim().isEmpty()) {
            predicates.add(cb.like(root.get("name"), "%" + keyword.trim() + "%"));
        }

        return predicates;
    }

    // Phương thức áp dụng sorting chung
    private void applySorting(CriteriaBuilder cb, CriteriaQuery<Story> query, Root<Story> root, Integer sort) {
        if (sort == null) {
            query.orderBy(cb.desc(root.get("updatedAt")));
            return;
        }

        switch (sort) {
            case 2 -> query.orderBy(cb.desc(root.get("createdAt")));
            case 10, 11, 12 -> query.orderBy(cb.desc(root.get("viewCount")));
            case 20 -> query.orderBy(cb.desc(root.get("follower")));
            case 21 -> query.orderBy(cb.desc(root.get("commentCount")));
            case 22 -> query.orderBy(cb.desc(root.get("newestChapter")));
            default -> query.orderBy(cb.desc(root.get("updatedAt")));
        }
    }
}
