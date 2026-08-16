package com.viettran.reading_story_web.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.viettran.reading_story_web.dto.request.GenreCreationRequest;
import com.viettran.reading_story_web.dto.request.GenreUpdationRequest;
import com.viettran.reading_story_web.dto.response.GenreResponse;
import com.viettran.reading_story_web.entity.mysql.Genre;
import com.viettran.reading_story_web.exception.AppException;
import com.viettran.reading_story_web.exception.ErrorCode;
import com.viettran.reading_story_web.mapper.GenreMapper;
import com.viettran.reading_story_web.repository.jpa.GenreRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GenreService {
    GenreRepository genreRepository;
    GenreMapper genreMapper;

    public List<GenreResponse> getAllGenres() {
        return genreRepository.findAll().stream()
                .map(genreMapper::toGenreResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public GenreResponse createGenre(GenreCreationRequest request) {
        Genre genre = genreMapper.toGenre(request);
        return genreMapper.toGenreResponse(genreRepository.save(genre));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public GenreResponse updateGenre(Integer id, GenreUpdationRequest request) {
        Genre genre = genreRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.GENRE_NOT_EXISTED));

        genreMapper.updateGenre(genre, request);

        return genreMapper.toGenreResponse(genreRepository.save(genre));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteGenre(Integer id) {
        genreRepository.deleteById(id);
    }

    public Set<Genre> getGenresByIds(Set<Integer> genreIds) {
        List<Genre> genres = genreRepository.findByIdIn(new ArrayList<>(genreIds));

        return new HashSet<>(genres); // Chuyển List thành Set
    }
}
