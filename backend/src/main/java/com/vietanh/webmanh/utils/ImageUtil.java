package com.vietanh.webmanh.utils;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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

    /** Resize giữ tỷ lệ */
    public static Mat downSizeKeepRatio(Mat src, int maxWidth, int maxHeight) {
        int originalWidth = src.cols();
        int originalHeight = src.rows();

        double ratio = Math.min(
                (double) maxWidth / originalWidth,
                (double) maxHeight / originalHeight
        );

        int newWidth = (int) (originalWidth * ratio);
        int newHeight = (int) (originalHeight * ratio);

        // Resize
        Mat resized = new Mat();
        Imgproc.resize(src, resized, new Size(newWidth, newHeight), 0, 0, Imgproc.INTER_AREA);

        // Sharpen : lấy lại biên sau khi downsize
        Mat kernel = new Mat(3, 3, CvType.CV_32F);
        kernel.put(0, 0,
                0, -0.5f,  0,
                -0.5f, 3f, -0.5f,
                0, -0.5f,  0
        );

        Mat sharpened = new Mat();
        Imgproc.filter2D(resized, sharpened, -1, kernel);

        return sharpened;
    }
}
