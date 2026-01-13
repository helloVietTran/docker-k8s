package com.vietanh.webmanh.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.codec.digest.DigestUtils;

public class ClientUtil {
    public static String getClientHash(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null) ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        // Hash chuỗi "ip + userAgent" để bảo mật và gọn nhẹ
        return DigestUtils.md5Hex(ip + userAgent);
    }
}
