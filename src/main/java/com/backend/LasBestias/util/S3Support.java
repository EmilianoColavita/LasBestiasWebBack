package com.backend.LasBestias.util;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.backend.LasBestias.model.Image;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.Date;

@Component
public class S3Support {


    private static String bucketName;

    private static AmazonS3 amazonS3 = null;

    public S3Support(AmazonS3 amazonS3, @Value("${aws.s3.bucket.name}") String bucketName) {
        S3Support.bucketName = bucketName;
        S3Support.amazonS3 = amazonS3;
    }

    public static PutObjectResult upload(String path, MultipartFile file) throws IllegalStateException {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());
        try {
            return amazonS3.putObject(bucketName, path, file.getInputStream(), objectMetadata);
        } catch (Exception e) {
            throw new IllegalStateException("AWS (S3) Failed to upload the file", e);
        }
    }

    public static String getS3url(Image image){
        Date expiration = getExpirationDate();
        String path;
        //aca saque cosas
        path = ImageUtil.buildPath(image);
        GeneratePresignedUrlRequest presignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, path)
                .withMethod(HttpMethod.GET)
                .withExpiration(expiration);
        URL url = amazonS3.generatePresignedUrl(presignedUrlRequest);
        return url.toString();
    }

    private static Date getExpirationDate(){
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 60;
        expiration.setTime(expTimeMillis);
        return expiration;
    }
}
