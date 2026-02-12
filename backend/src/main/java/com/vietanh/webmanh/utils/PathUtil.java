package com.vietanh.webmanh.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class PathUtil {
    /**
     * Converts a file system path to a URL path format.
     * Used for formatting API responses.
     *
     * @param fsPath The system-dependent file path.
     * @return A cross-platform URL path string.
     */
    public static String toUrlPath(String fsPath) {
        if (fsPath == null) return null;
        return fsPath.replace(File.separatorChar, '/');
    }

    /**
     * Converts a URL path back to a file system path.
     * Used when locating files based on a URL string.
     *
     * @param urlPath The URL-formatted path.
     * @return A {@link Path} object compatible with the host OS.
     */
    public static Path toFsPathFromUrl(String urlPath) {
        if (urlPath == null) return null;
        return Path.of(urlPath.replace('/', File.separatorChar));
    }

}
