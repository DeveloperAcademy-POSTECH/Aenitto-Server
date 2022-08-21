package com.firefighter.aenitto.messages.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@RequiredArgsConstructor
@Service
@Qualifier(value = "StorageS3ServiceImpl")
public class StorageS3ServiceImpl implements StorageService {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Override
    public void upload(String imageName, InputStream inputStream, ObjectMetadata objectMetadata) {
        amazonS3Client.putObject(new PutObjectRequest(
                bucketName,
                imageName,
                inputStream,
                objectMetadata)
                .withCannedAcl(CannedAccessControlList.PublicRead)
        );
    }

    @Override
    public String getUrl(String imageName) {
        return amazonS3Client.getUrl(bucketName, imageName).toString();
    }
}
