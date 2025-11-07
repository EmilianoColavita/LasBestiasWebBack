package com.backend.LasBestias.service;

import com.backend.LasBestias.model.ImageType;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    Long uploadImage(MultipartFile file, ImageType type, Long modelId);

    String getS3url(Long modelId, ImageType type);

    void deleteImage(Long modelId, ImageType type);
}
