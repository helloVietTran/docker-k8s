package com.vietanh.webmanh.services.impl;

import com.vietanh.webmanh.constants.ErrorCode;
import com.vietanh.webmanh.dbs.postgres.models.Chapter;
import com.vietanh.webmanh.dbs.postgres.models.ChapterImage;
import com.vietanh.webmanh.dbs.postgres.models.Comic;
import com.vietanh.webmanh.dbs.postgres.models.User;
import com.vietanh.webmanh.dbs.postgres.repositories.ChapterImageRepository;
import com.vietanh.webmanh.dbs.postgres.repositories.ChapterRepository;
import com.vietanh.webmanh.dbs.postgres.repositories.ComicRepository;
import com.vietanh.webmanh.dtos.requests.ChapterRequest;
import com.vietanh.webmanh.dtos.requests.UpdateChapterRequest;
import com.vietanh.webmanh.dtos.responses.ChapterImageDTO;
import com.vietanh.webmanh.dtos.responses.ChapterResponse;
import com.vietanh.webmanh.exception.AppException;
import com.vietanh.webmanh.mappers.ChapterMapper;
import com.vietanh.webmanh.services.ChapterService;
import com.vietanh.webmanh.utils.AuthUtil;
import com.vietanh.webmanh.utils.DateTimeFormatUtil;
import com.vietanh.webmanh.utils.ImageUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChapterServiceImpl implements ChapterService {
    ComicRepository comicRepository;
    ChapterRepository chapterRepository;
    ChapterImageRepository chapterImageRepository;

    ChapterMapper chapterMapper;

    @NonFinal
    @Value("${app.image-root}")
    String imageRoot;

    @Override
    @Transactional
    public ChapterResponse createChapter(Integer comicId, ChapterRequest request) {

        Comic comic = findComicAndValidateAuthor(comicId);
        validateImages(request.getImageFiles());

        Chapter chapter = chapterMapper.toChapter(request);
        chapter.generateSelfSlug();
        chapter.setComic(comic);

        try {
            List<ChapterImage> images =
                    saveChapterImages(chapter, comic, request.getImageFiles());

            chapter.setChapterImages(images);

            comicRepository.save(comic);

            Chapter saved = chapterRepository.save(chapter);
            return this.mapToChapterResponse(saved);
        } catch (IOException e) {
            throw new AppException(ErrorCode.FILE_STORAGE_ERROR);
        }
    }


    @Override
    @Transactional
    public ChapterResponse updateChapter(
            Integer comicId,
            Integer chapterId,
            UpdateChapterRequest request
    ) {
        Comic comic = this.findComicAndValidateAuthor(comicId);
        this.validateImages(request.getImageFiles());

        Chapter chapter = chapterRepository.findByIdWithImages(chapterId)
                .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_EXISTED));

        // update chapter info (nếu có)
        chapter.setChapterName(request.getChapterName());
        chapter.setChapterNo(request.getChapterNo());
        chapter.generateSelfSlug();

        // 1. delete old images (disk)
        this.deleteChapterImagesOnDisk(chapter);

        // 2. delete old images (db)
        chapter.getChapterImages().clear();

        try {
            // 3. create new images
            List<ChapterImage> newImages =
                    saveChapterImages(chapter, comic, request.getImageFiles());

            chapter.setChapterImages(newImages);

            Chapter saved = chapterRepository.save(chapter);
            return this.mapToChapterResponse(saved);
        } catch (IOException e) {
            throw new AppException(ErrorCode.FILE_STORAGE_ERROR);
        }
    }


    @Override
    @Transactional
    public void deleteChapter(Integer comicId, Integer chapterId) {
        this.findComicAndValidateAuthor(comicId);

        Chapter chapter = chapterRepository.findByIdWithImages(chapterId)
                .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_EXISTED));

        // 1. delete images on disk
        this.deleteChapterImagesOnDisk(chapter);

        // 2. delete chapter
        chapterRepository.delete(chapter);
    }


    @Override
    public ChapterResponse getChapterById(Integer chapterId) {
        Chapter chapter = chapterRepository.findByIdWithImages(chapterId)
                .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_EXISTED));

        return this.mapToChapterResponse(chapter);
    }

    private Comic findComicAndValidateAuthor(Integer comicId) {
        Integer currentUserId = AuthUtil.getCurrentUserId();

        Comic comic = comicRepository.findById(comicId)
                .orElseThrow(() -> new AppException(ErrorCode.COMIC_NOT_EXISTED));

        User author = comic.getAuthor();
        if (!author.getUserId().equals(currentUserId)
                && !AuthUtil.hasRole("ROLE_ADMIN")) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return comic;
    }

    private void validateImages(List<MultipartFile> files) {
        for (MultipartFile file : files) {
            if (!ImageUtil.isValidImage(file)) {
                throw new AppException(ErrorCode.REQUIRED_IMAGE);
            }
        }
    }

    private void deleteChapterImagesOnDisk(Chapter chapter) {
        Path root = Paths.get(imageRoot);

        try {
            for (ChapterImage image : chapter.getChapterImages()) {
                Files.deleteIfExists(root.resolve(image.getChapterImageSrc()));
            }
        } catch (IOException e) {
            log.error("Failed to delete images of chapterId={}", chapter.getChapterId(), e);
            throw new AppException(ErrorCode.DELETED_FILE_ERROR);
        }
    }


    private List<ChapterImage> saveChapterImages(
            Chapter chapter,
            Comic comic,
            List<MultipartFile> files
    ) throws IOException {

        Path root = Paths.get(imageRoot);
        Path chapterDir = root
                .resolve("comic")
                .resolve(comic.getSlug())
                .resolve(chapter.getSlug());

        Files.createDirectories(chapterDir);

        List<ChapterImage> images = new ArrayList<>();
        List<Path> savedFiles = new ArrayList<>();

        int index = 1;

        try {
            for (MultipartFile file : files) {
                String fileName = String.format(
                        "%s_%03d.jpg",
                        comic.getSlug(),
                        index++
                );

                Path filePath = chapterDir.resolve(fileName);

                // save image on disk
                ImageUtil.saveJpg(
                        ImageUtil.multipartToMat(file),
                        filePath
                );

                savedFiles.add(filePath);

                images.add(
                        ChapterImage.builder()
                                .chapter(chapter)
                                .chapterImageName(fileName)
                                .chapterImageSrc(
                                        root.relativize(filePath).toString()
                                )
                                .build()
                );
            }
            return images;

        } catch (Exception e) {
            // rollback filesystem
            for (Path path : savedFiles) {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException ex) {
                    log.warn("Failed to cleanup image file {}", path, ex);
                }
            }
            throw e;
        }
    }


    private ChapterResponse mapToChapterResponse(Chapter chapter) {
        ChapterResponse res = chapterMapper.toChapterResponse(chapter);

        if (chapter.getChapterImages() != null && !chapter.getChapterImages().isEmpty()) {

            List<ChapterImageDTO> imageResponses = chapter.getChapterImages()
                    .stream()
                    .map(image -> {
                        ChapterImageDTO imgRes = new ChapterImageDTO();
                        imgRes.setChapterImageId(
                                image.getChapterImageId() == null
                                        ? null
                                        : image.getChapterImageId().toString()
                        );
                        imgRes.setChapterImageName(image.getChapterImageName());
                        imgRes.setChapterImageSrc(image.getChapterImageSrc());
                        return imgRes;
                    })
                    .toList();

            res.setChapterImages(imageResponses);
        } else {
            res.setChapterImages(List.of());
        }

        DateTimeFormatUtil dateTimeFormatUtil = new DateTimeFormatUtil();
        res.setCreatedAt(dateTimeFormatUtil.format(chapter.getCreatedAt()));
        res.setUpdatedAt(dateTimeFormatUtil.format(chapter.getUpdatedAt()));

        return res;
    }

}
