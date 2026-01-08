package com.vietanh.webmanh.services;

import com.vietanh.webmanh.constants.AdminDecision;
import com.vietanh.webmanh.dtos.responses.ComicResponse;

public interface AdminService {
    ComicResponse approveComic(Integer comicId, AdminDecision adminDecision);
}
