package com.backend.LasBestias.service.impl;

import com.backend.LasBestias.model.Image;
import com.backend.LasBestias.model.ImageType;
import com.backend.LasBestias.repository.ImageRepository;
import com.backend.LasBestias.service.ImageService;
import com.backend.LasBestias.util.ImageUtil;
import com.backend.LasBestias.util.S3Support;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ImageServiceImpl implements ImageService {

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    private final ImageRepository imageRepository;

    public ImageServiceImpl(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @Override
    public Long uploadImage(MultipartFile file, ImageType type, Long modelId) {
        Image image = ImageUtil.createImage(file, type, modelId);
        image = imageRepository.save(image);
        S3Support.upload(ImageUtil.buildPath(image), file);
        return image.getId();
    }

    @Override
    public String getS3url(Long modelId, ImageType type) {
        List<Image> imageList = imageRepository.findByModelIdAndType(modelId, type);
        if (imageList.isEmpty()) {
            return null;
        }
        return S3Support.getS3url(imageList.get(0));
    }

    @Override
    public void deleteImage(Long modelId, ImageType type) {
        List<Image> imageList = imageRepository.findByModelIdAndType(modelId, type);
        imageList.forEach(imageRepository::delete);
    }
}
