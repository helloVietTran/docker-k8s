package com.vietanh.webmanh.services.impl;

import com.vietanh.webmanh.dbs.postgres.models.Genre;
import com.vietanh.webmanh.dbs.postgres.repositories.GenreRepository;
import com.vietanh.webmanh.dtos.requests.UpdateGenreRequest;
import com.vietanh.webmanh.dtos.responses.GenreResponse;
import com.vietanh.webmanh.mappers.GenreMapper;
import com.vietanh.webmanh.services.GenreService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GenreServiceImpl implements GenreService {

    GenreRepository genreRepository;
    GenreMapper genreMapper;

    @Override
    public List<GenreResponse> getGenres() {
        return genreRepository.findAll()
                .stream()
                .map(genreMapper::toGenreResponse)
                .toList();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public GenreResponse updateGenre(Integer genreId, UpdateGenreRequest request) {
        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Genre not found with id: " + genreId)
                );

        genreMapper.updateGenre(genre, request);

        Genre savedGenre = genreRepository.save(genre);

        return genreMapper.toGenreResponse(savedGenre);
    }
}
