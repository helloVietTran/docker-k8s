package com.vietanh.webmanh.dbs.postgres.specs;

import com.vietanh.webmanh.constants.AdminDecision;
import com.vietanh.webmanh.constants.Gender;
import com.vietanh.webmanh.constants.ComicStatus;
import com.vietanh.webmanh.dbs.postgres.models.Comic;
import com.vietanh.webmanh.dbs.postgres.models.Genre;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ComicSearchSpec {

    private final List<Specification<Comic>> specs = new ArrayList<>();

    private ComicSearchSpec() {}

    public static ComicSearchSpec builder() {
        return new ComicSearchSpec();
    }

    // sử dụng tên biến trong ORM
    public ComicSearchSpec filterByGenres(List<Integer> genreCodes) {
        if (genreCodes == null || genreCodes.isEmpty()) return this;

        specs.add((root, query, cb) -> {
            query.distinct(true);
            Join<Comic, Genre> join = root.join("genres");
            return join.get("code").in(genreCodes);
        });
        return this;
    }

    public ComicSearchSpec onlyApproved() {
        specs.add((root, query, cb) ->
                root.get("adminDecision").in(
                        AdminDecision.APPROVED
                )
        );
        return this;
    }

    public ComicSearchSpec filterByNotGenres(List<Integer> notGenreCodes) {
        if (notGenreCodes == null || notGenreCodes.isEmpty()) return this;

        specs.add((root, query, cb) -> {
            Subquery<Integer> sub = query.subquery(Integer.class);
            Root<Comic> subRoot = sub.from(Comic.class);
            Join<Comic, Genre> subGenre = subRoot.join("genres");

            sub.select(subRoot.get("comicId"))
                    .where(subGenre.get("code").in(notGenreCodes));

            return cb.not(root.get("comicId").in(sub));
        });
        return this;
    }

    public ComicSearchSpec filterByStatus(ComicStatus status) {
        if (status == null) return this;

        specs.add((root, query, cb) ->
                cb.equal(root.get("status"), status)
        );
        return this;
    }

    public ComicSearchSpec filterByGender(Gender gender) {
        if (gender == null) return this;

        specs.add((root, query, cb) ->
                cb.equal(root.get("gender"), gender)
        );
        return this;
    }

    public ComicSearchSpec filterByMinChapter(Integer minChapter) {
        if (minChapter == null) return this;

        specs.add((root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("newestChapter"), minChapter)
        );
        return this;
    }

    // dùng hàm unaccent để bỏ dấu
    public ComicSearchSpec filterByKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) return this;

        String like = "%" + keyword.toLowerCase() + "%";

        specs.add((root, query, cb) -> {
            Expression<String> comicName = cb.function(
                    "unaccent",
                    String.class,
                    cb.lower(root.get("comicName"))
            );

            Expression<String> otherName = cb.function(
                    "unaccent",
                    String.class,
                    cb.lower(root.get("otherName"))
            );

            Expression<String> keywordExpr = cb.function(
                    "unaccent",
                    String.class,
                    cb.literal(like)
            );

            return cb.or(
                    cb.like(comicName, keywordExpr),
                    cb.like(otherName, keywordExpr)
            );
        });

        return this;
    }

    public Specification<Comic> build() {
        return specs.stream()
                .reduce(Specification::and)
                .orElse(null);
    }
}
