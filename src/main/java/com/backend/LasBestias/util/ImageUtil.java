package com.backend.LasBestias.util;

import com.backend.LasBestias.model.Image;
import com.backend.LasBestias.model.ImageType;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public class ImageUtil {

    private static final String slash = "/";
    private static final String MAIN_FOLDER = "images";

    public static String buildPath(Image image) {
        return MAIN_FOLDER +
                slash +
                image.getType() +
                slash +
                image.getUserId() +
                slash +
                image.getS3name();
    }

    public static Image createImage(MultipartFile file, ImageType imageType, Long modelId) {
        Image image = new Image();
        image.setSize(file.getSize());
        image.setExtension(getFileExtension(file));
        image.setOriginalName(file.getOriginalFilename());
        image.setS3name(UUID.randomUUID());
        image.setType(imageType);
        image.setModelId(modelId);
        return image;
    }

    private static String getFileExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null && originalFilename.contains(".")) {
            return originalFilename.substring(originalFilename.lastIndexOf('.') + 1);
        }
        return "";
    }
}
