package com.vietanh.webmanh.services.impl;

import com.vietanh.webmanh.constants.*;
import com.vietanh.webmanh.dbs.postgres.models.Comic;
import com.vietanh.webmanh.dbs.postgres.models.Genre;
import com.vietanh.webmanh.dbs.postgres.models.PublishCalendar;
import com.vietanh.webmanh.dbs.postgres.models.User;
import com.vietanh.webmanh.dbs.postgres.repositories.ComicRepository;
import com.vietanh.webmanh.dbs.postgres.repositories.GenreRepository;
import com.vietanh.webmanh.dbs.postgres.repositories.PublishCalendarRepository;
import com.vietanh.webmanh.dbs.postgres.repositories.UserRepository;
import com.vietanh.webmanh.dtos.requests.ComicRequest;
import com.vietanh.webmanh.dtos.requests.UpdateComicRequest;
import com.vietanh.webmanh.dtos.responses.ComicResponse;
import com.vietanh.webmanh.dtos.responses.PageResponse;
import com.vietanh.webmanh.exception.AppException;
import com.vietanh.webmanh.mappers.ComicMapper;
import com.vietanh.webmanh.services.ComicService;
import com.vietanh.webmanh.dbs.postgres.specs.ComicSearchSpec;
import com.vietanh.webmanh.dbs.postgres.specs.ComicSort;
import com.vietanh.webmanh.utils.AuthUtil;
import com.vietanh.webmanh.utils.ImageUtil;
import com.vietanh.webmanh.utils.PathUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Mat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
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
    PublishCalendarRepository publishCalendarRepo;

    ComicMapper comicMapper;

    @NonFinal
    @Value("${app.image-root}")
    String imageRoot;

    @Override
    public ComicResponse createComic(ComicRequest request) {

        Integer userId = AuthUtil.getCurrentUserId();

        // 1. Validate image
        if (!ImageUtil.isValidImage(request.getCoverImage())) {
            throw new AppException(ErrorCode.REQUIRED_IMAGE);
        }

        Comic comic = comicMapper.toComic(request);

        // 2. find genres
        Set<Genre> genres = genreRepository.findByCodeIn(request.getGenreCodes());
        comic.setGenres(genres);

        comic.generateSelfComicSlug();

        // 3. Find author
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        comic.setAuthor(user);

        try {
            // 4. Process image
            List<String> coverSrc = processComicCoverImage(
                    request.getCoverImage(),
                    comic.getSlug(),
                    null
            );
            comic.setCoverSrc(coverSrc);

            Comic savedComic = comicRepository.save(comic);

            // create publish calendar
            PublishCalendar publishCalendar = PublishCalendar.builder()
                    .publishAt(request.getPublishAt())
                    .targetId(savedComic.getComicId())
                    .publishStatus(PublishStatus.SCHEDULED)
                    .publishTargetType(PublishTargetType.COMIC)
                    .createdBy(userId)
                    .createdAt(Instant.now())
                    .build();
            publishCalendarRepo.save(publishCalendar);

            // mapping response
            ComicResponse response = comicMapper.toComicResponse(savedComic);
            response.setAuthorName(user.getUsername());
            response.setCoverSrc(
                    savedComic.getCoverSrc()
                            .stream()
                            .map(PathUtil::toUrlPath)
                            .toList()
            );

            return response;
        } catch (IOException e) {
            log.error("ACTION=CREATE_COMIC_IMAGE STATUS=FAILED slug={}", comic.getSlug());
            throw new AppException(ErrorCode.FILE_STORAGE_ERROR);
        }
    }

    @Override
    public ComicResponse updateComic(UpdateComicRequest request, Integer comicId) {
        Integer userId = AuthUtil.getCurrentUserId();

        Comic comic = comicRepository.findById(comicId)
                .orElseThrow(() -> new AppException(ErrorCode.COMIC_NOT_EXISTED));

        User user = comic.getAuthor();

        // nếu là tác giả của truyện hoặc admin thì cho phép sửa
        if(!user.getUserId().equals(userId) || AuthUtil.hasRole("ROLE_ADMIN") )
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        comicMapper.updateComic(request, comic);

        try {
            if (ImageUtil.isValidImage(request.getCoverImage())) {
                comic.generateSelfComicSlug(); // gen lại slug, nếu đổi tên
                List<String> newCoverSrc = this.processComicCoverImage(
                        request.getCoverImage(),
                        comic.getSlug(),
                        comic.getCoverSrc()
                );

                comic.setCoverSrc(newCoverSrc);
            }

            Comic savedComic = comicRepository.save(comic);

            ComicResponse response = comicMapper.toComicResponse(savedComic);
            if (savedComic.getCoverSrc() != null) {
                response.setCoverSrc(
                        savedComic.getCoverSrc().stream()
                                .map(PathUtil::toUrlPath)
                                .toList()
                );
            }
            response.setAuthorName(user.getUsername());
            return response;

        } catch (IOException e) {
            log.error(
                    "ACTION=UPDATE_COMIC_IMAGE STATUS=FAILED comicId={} slug={}",
                    comicId,
                    comic.getSlug()
            );
            throw new AppException(ErrorCode.FILE_STORAGE_ERROR);

        }
    }

    @Override
    public ComicResponse getComicById(Integer comicId) {
        Comic comic = comicRepository.findByComicIdAndAdminDecisionIn(
                        comicId,
                        List.of(AdminDecision.APPROVED))
                .orElseThrow(() -> new AppException(ErrorCode.COMIC_NOT_EXISTED));

        ComicResponse response = comicMapper.toComicResponse(comic);
        response.setAuthorName(comic.getAuthor().getUsername());
        if (comic.getCoverSrc() != null) {
            response.setCoverSrc(
                    comic.getCoverSrc().stream()
                            .map(PathUtil::toUrlPath)
                            .toList()
            );
        }

        return response;
    }

    @Override
    public void deleteComic(Integer comicId) {

        Integer userId = AuthUtil.getCurrentUserId();

        Comic comic = comicRepository.findById(comicId)
                .orElseThrow(() -> new AppException(ErrorCode.COMIC_NOT_EXISTED));

        User author = comic.getAuthor();
        if (author == null) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }


        if (!author.getUserId().equals(userId) && !AuthUtil.hasRole("ADMIN")) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        if (comic.getCoverSrc() != null) {
            Path root = Paths.get(imageRoot);
            for (String pathStr : comic.getCoverSrc()) {
                try {
                    Files.deleteIfExists(root.resolve(pathStr));
                } catch (IOException e) {
                    log.error(
                            "ACTION=DELETE_COMIC_IMAGE STATUS=FAILED comicId={} path={}",
                            comicId,
                            pathStr
                    );
                }
            }
        }

        comicRepository.delete(comic);
    }

    @Override
    public PageResponse<ComicResponse> searchComics(
            String keyword,
            List<Integer> genreCodes,
            List<Integer> notGenreCodes,
            ComicStatus status,
            Integer minChapter,
            Gender gender,
            ComicSortType sortOption,
            Pageable pageable) {

        Specification<Comic> spec = ComicSearchSpec.builder()
                .onlyApproved() // chỉ được chấp nhận mới trả về
                .filterByKeyword(keyword)
                .filterByGenres(genreCodes)
                .filterByNotGenres(notGenreCodes)
                .filterByStatus(status)
                .filterByMinChapter(minChapter)
                .filterByGender(gender)
                .build();

        Sort sortOrder = ComicSort.builder()
                .apply(sortOption)
                .build();

        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sortOrder
        );

        try {
            Page<Comic> page = comicRepository.findAll(spec, pageable);
            List<ComicResponse> data = page.getContent()
                    .stream()
                    .map(comicMapper::toComicResponse)
                    .peek(res -> {
                        if (res.getCoverSrc() != null) {
                            res.setCoverSrc(
                                    res.getCoverSrc().stream()
                                            .map(PathUtil::toUrlPath)
                                            .toList()
                            );
                        }
                    })
                    .toList();

            return PageResponse.<ComicResponse>builder()
                    .currentPage(page.getNumber())
                    .pageSize(page.getSize())
                    .totalPages(page.getTotalPages())
                    .totalElements(page.getTotalElements())
                    .data(data)
                    .build();
        } catch (Exception e) {
            log.error("Search comic failed", e);
            throw e;
        }


    }

    /**
     * Processes a comic cover image by generating resized versions (original, thumbnail, slider)
     * and safely replacing existing cover images using a temporary directory.
     *
     * @param coverImage  the uploaded cover image
     * @param comicSlug   the comic slug used to determine the storage path
     * @param oldCoverSrc a list of existing cover image paths to be deleted; may be null or empty
     *
     * @return a list of relative paths of the newly generated cover images
     *
     * @throws IOException if an I/O error occurs during image processing or file operations
     */
    private List<String> processComicCoverImage(
            MultipartFile coverImage,
            String comicSlug,
            List<String> oldCoverSrc
    ) throws IOException  {

        Path root = Paths.get(imageRoot);
        Path finalDir = root.resolve("comic").resolve(comicSlug);
        Path tempDir = finalDir.resolve("comic/_tmp");

        List<Path> createdFiles = new ArrayList<>();

        try {
            // 1. Tạo thư mục temp
            Files.createDirectories(tempDir);

            Mat originalMat = ImageUtil.multipartToMat(coverImage);

            // 2. ORIGINAL
            Path originalPath = tempDir.resolve(ResizeImageOptions.ORIGINAL.getImageName());
            ImageUtil.saveJpg(originalMat, originalPath);
            createdFiles.add(originalPath);

            // 3. THUMBNAIL
            Mat thumbnail = ImageUtil.downSizeKeepRatio(
                    originalMat,
                    ResizeImageOptions.THUMBNAIL_COVER.getWidth(),
                    ResizeImageOptions.THUMBNAIL_COVER.getHeight()
            );
            Path thumbnailPath = tempDir.resolve(ResizeImageOptions.THUMBNAIL_COVER.getImageName());
            ImageUtil.saveJpg(thumbnail, thumbnailPath);
            createdFiles.add(thumbnailPath);

            // 4. SLIDER
            Mat slider = ImageUtil.downSizeKeepRatio(
                    originalMat,
                    ResizeImageOptions.SLIDER_COVER.getWidth(),
                    ResizeImageOptions.SLIDER_COVER.getHeight()
            );
            Path sliderPath = tempDir.resolve(ResizeImageOptions.SLIDER_COVER.getImageName());
            ImageUtil.saveJpg(slider, sliderPath);
            createdFiles.add(sliderPath);

            // 5. Nếu OK hết → xóa ảnh cũ
            if (oldCoverSrc != null && !oldCoverSrc.isEmpty()) {
                for (String pathStr : oldCoverSrc) {
                    Files.deleteIfExists(root.resolve(pathStr));
                }
            }

            // 6. Move file từ temp -> final
            Files.createDirectories(finalDir);

            List<String> coverSrc = new ArrayList<>();
            for (Path tempFile : createdFiles) {
                Path finalPath = finalDir.resolve(tempFile.getFileName());
                Files.move(tempFile, finalPath, StandardCopyOption.REPLACE_EXISTING);
                coverSrc.add(root.relativize(finalPath).toString());
            }

            // 7. Xóa thư mục temp
            Files.deleteIfExists(tempDir);

            return coverSrc;

        } catch (IOException e) {
            // ROLLBACK: xóa file vừa tạo
            for (Path path : createdFiles) {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException ex) {
                    log.warn("Rollback delete failed: {}", path, ex);
                }
            }
            try {
                Files.deleteIfExists(tempDir);
            } catch (IOException ex) {
                log.warn("Failed to delete temp dir: {}", tempDir, ex);
            }
            throw e;
        }
    }
}
