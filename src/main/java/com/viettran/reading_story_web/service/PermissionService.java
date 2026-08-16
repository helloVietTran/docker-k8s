package com.viettran.reading_story_web.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.viettran.reading_story_web.dto.request.PermissionRequest;
import com.viettran.reading_story_web.dto.response.PermissionResponse;
import com.viettran.reading_story_web.entity.mysql.Permission;
import com.viettran.reading_story_web.mapper.PermissionMapper;
import com.viettran.reading_story_web.repository.jpa.PermissionRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionService {
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    // @PreAuthorize("hasRole('ADMIN')")
    public PermissionResponse create(PermissionRequest request) {
        Permission permission = permissionMapper.toPermission(request);
        permission = permissionRepository.save(permission);
        return permissionMapper.toPermissionResponse(permission);
    }

    // @PreAuthorize("hasRole('ADMIN')")
    public List<PermissionResponse> getAll() {
        var permissions = permissionRepository.findAll();
        return permissions.stream().map(permissionMapper::toPermissionResponse).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void delete(String permission) {
        permissionRepository.deleteById(permission);
    }
}
