package com.vietanh.webmanh.utils;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.MatOfInt;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ImageUtil {

    public static boolean isValidImage(MultipartFile file) {
        try {
            Mat mat = multipartToMat(file);
            return !mat.empty();
        } catch (Exception e) {
            return false;
        }
    }

    public static Mat multipartToMat(MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();
        Mat mat = Imgcodecs.imdecode(new MatOfByte(bytes), Imgcodecs.IMREAD_UNCHANGED);
        if (mat.empty()) {
            throw new IllegalArgumentException("Invalid image");
        }
        return mat;
    }

    public static void saveJpg(Mat image, Path path) throws IOException {
        Files.createDirectories(path.getParent());

        // quality 0-100
        MatOfInt params = new MatOfInt(Imgcodecs.IMWRITE_JPEG_QUALITY, 100);
        Imgcodecs.imwrite(path.toString(), image, params);
    }

    /** Resize giữ tỷ lệ, width hoặc height sẽ bị giới hạn, chiều còn lại tính theo tỷ lệ */
    public static Mat resizeKeepRatio(Mat src, int maxWidth, int maxHeight) {
        int originalWidth = src.cols();
        int originalHeight = src.rows();

        double ratio = Math.min((double) maxWidth / originalWidth, (double) maxHeight / originalHeight);

        int newWidth = (int) (originalWidth * ratio);
        int newHeight = (int) (originalHeight * ratio);

        Mat dst = new Mat();
        Imgproc.resize(src, dst, new Size(newWidth, newHeight), 0, 0, Imgproc.INTER_LANCZOS4);
        return dst;
    }
}
