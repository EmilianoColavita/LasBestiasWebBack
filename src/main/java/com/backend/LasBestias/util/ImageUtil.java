package com.backend.LasBestias.util;

import com.backend.LasBestias.model.Image;
import com.backend.LasBestias.model.ImageType;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public class ImageUtil {

    private static final String SLASH = "/";
    private static final String MAIN_FOLDER = "images";

    public static String buildPath(Image image) {
        return MAIN_FOLDER +
                SLASH +
                image.getType() +
                SLASH +
                image.getModelId() +
                SLASH +
                image.getS3name() +
                "." +
                image.getExtension(); // 🔥 IMPORTANTE: agregamos extensión
    }

    public static Image createImage(MultipartFile file, ImageType imageType, Long modelId) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío");
        }

        Image image = new Image();
        image.setSize(file.getSize());
        image.setExtension(getFileExtension(file)); // 🔥 validamos correctamente
        image.setOriginalName(file.getOriginalFilename());
        image.setS3name(UUID.randomUUID());
        image.setType(imageType);
        image.setModelId(modelId);

        return image;
    }

    private static String getFileExtension(MultipartFile file) {

        String contentType = file.getContentType();

        if (contentType == null) {
            throw new IllegalArgumentException("Tipo de archivo inválido");
        }

        switch (contentType) {
            case "image/png":
                return "png";

            case "image/jpeg":
            case "image/jpg":
                return "jpg";

            default:
                throw new IllegalArgumentException("Solo se permiten imágenes JPG o PNG");
        }
    }
}