package com.vietanh.webmanh.services.impl;

import com.vietanh.webmanh.constants.AdminDecision;
import com.vietanh.webmanh.constants.ErrorCode;
import com.vietanh.webmanh.dbs.postgres.models.Comic;
import com.vietanh.webmanh.dbs.postgres.repositories.ComicRepository;
import com.vietanh.webmanh.dtos.responses.ComicResponse;
import com.vietanh.webmanh.exception.AppException;
import com.vietanh.webmanh.mappers.ComicMapper;
import com.vietanh.webmanh.services.AdminService;
import com.vietanh.webmanh.utils.PathUtil;

public class AdminServiceImpl implements AdminService {
    ComicRepository comicRepository;

    ComicMapper comicMapper;

    @Override
    public ComicResponse approveComic(Integer comicId, AdminDecision adminDecision) {
        Comic comic = comicRepository.findById(comicId)
                .orElseThrow(()-> new AppException(ErrorCode.COMIC_NOT_EXISTED));

        comic.setAdminDecision(adminDecision);

        Comic savedComic = comicRepository.save(comic);

        ComicResponse response = comicMapper.toComicResponse(savedComic);
        if (savedComic.getCoverSrc() != null) {
            response.setCoverSrc(
                    savedComic.getCoverSrc().stream()
                            .map(PathUtil::toUrlPath)
                            .toList()
            );
        }
        response.setAuthorName(comic.getAuthor().getUsername());
        return response;
    }
}
