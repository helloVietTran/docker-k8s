package com.vietanh.webmanh.services.impl;

import com.vietanh.webmanh.constants.ErrorCode;
import com.vietanh.webmanh.constants.ResizeImageOptions;
import com.vietanh.webmanh.dbs.postgres.models.Comic;
import com.vietanh.webmanh.dbs.postgres.models.Genre;
import com.vietanh.webmanh.dbs.postgres.models.User;
import com.vietanh.webmanh.dbs.postgres.repositories.ComicRepository;
import com.vietanh.webmanh.dbs.postgres.repositories.GenreRepository;
import com.vietanh.webmanh.dbs.postgres.repositories.UserRepository;
import com.vietanh.webmanh.dtos.requests.ComicRequest;
import com.vietanh.webmanh.dtos.responses.ComicResponse;
import com.vietanh.webmanh.dtos.responses.PageResponse;
import com.vietanh.webmanh.exception.AppException;
import com.vietanh.webmanh.mappers.ComicMapper;
import com.vietanh.webmanh.services.ComicService;
import com.vietanh.webmanh.utils.AuthUtil;
import com.vietanh.webmanh.utils.ImageUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Mat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ComicServiceImpl implements ComicService {
    ComicRepository comicRepository;
    GenreRepository genreRepository;
    UserRepository userRepository;

    ComicMapper comicMapper;

    @NonFinal
    @Value("${app.image-root}")
    String imageRoot;

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'AUTHOR')")
    public ComicResponse createComic(ComicRequest request) {
        Integer userId = AuthUtil.getCurrentUserId();

        if (!ImageUtil.isValidImage(request.getCoverImage())) {
            throw new AppException(ErrorCode.REQUIRED_IMAGE);
        }

        Comic comic = comicMapper.toComic(request);

        // find genre
        Set<Genre> genres = genreRepository.findByCodeIn(request.getGenreCodes());
        comic.setGenres(genres);

        // generate slug
        comic.generateSelfComicSlug();

        // find user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        comic.setAuthor(user);

        List<String> coverSrc = new ArrayList<>();

        Path root = Paths.get(imageRoot);
        Path basePath = root.resolve("comic/" + comic.getSlug() + "/");

        try {
            Files.createDirectories(basePath);

            Mat originalMat = ImageUtil.multipartToMat(request.getCoverImage());

            // save original
            Path originalPath = basePath.resolve(ResizeImageOptions.ORIGINAL.getImageName());
            ImageUtil.saveJpg(originalMat, originalPath);
            coverSrc.add(toForwardSlashPath(root.relativize(originalPath)));

            // thumbnail
            Mat thumbnail = ImageUtil.resizeKeepRatio(
                    originalMat,
                    ResizeImageOptions.THUMBNAIL_COVER.getWidth(),
                    ResizeImageOptions.THUMBNAIL_COVER.getHeight()
            );
            Path thumbnailPath = basePath.resolve(ResizeImageOptions.THUMBNAIL_COVER.getImageName());
            ImageUtil.saveJpg(thumbnail, thumbnailPath);
            coverSrc.add(toForwardSlashPath(root.relativize(thumbnailPath)));

            // slider
            Mat slider = ImageUtil.resizeKeepRatio(
                    originalMat,
                    ResizeImageOptions.SLIDER_COVER.getWidth(),
                    ResizeImageOptions.SLIDER_COVER.getHeight()
            );
            Path sliderPath = basePath.resolve(ResizeImageOptions.SLIDER_COVER.getImageName());
            ImageUtil.saveJpg(slider, sliderPath);
            coverSrc.add(toForwardSlashPath(root.relativize(sliderPath)));

            comic.setCoverSrc(coverSrc);

            // save comic
            Comic savedComic = comicRepository.save(comic);

            // map to response
            ComicResponse response = comicMapper.toComicResponse(savedComic);
            response.setCoverSrc(comic.getCoverSrc() != null ? new ArrayList<>(comic.getCoverSrc()) : null);

            return response;

        } catch (Exception e) {
            log.error("ACTION=SAVE_IMAGE STATUS=FAILED path={} reason={} message={}",
                    basePath, e.getClass().getSimpleName(), e.getMessage(), e);

            // rollback: xóa tất cả file đã tạo
            for (String pathStr : coverSrc) {
                try {
                    Files.deleteIfExists(root.resolve(pathStr));
                } catch (IOException ex) {
                    log.warn("Failed to delete file during rollback: {}", pathStr, ex);
                }
            }
            try {
                Files.deleteIfExists(basePath);
            } catch (IOException ex) {
                log.warn("Failed to delete directory during rollback: {}", basePath, ex);
            }

            throw new AppException(ErrorCode.FILE_STORAGE_ERROR);
        }
    }

    private String toForwardSlashPath(Path path) {
        return path.toString().replace(File.separatorChar, '/');
    }


    @Override
    public ComicResponse updateComic(ComicRequest request) {
        return null;
    }

    @Override
    public void deleteComic(ComicRequest request) {

    }

    @Override
    public PageResponse<ComicResponse> getComics() {
        return null;
    }
}
