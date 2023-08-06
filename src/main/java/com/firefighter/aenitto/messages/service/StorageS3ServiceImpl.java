package com.firefighter.aenitto.messages.service;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@RequiredArgsConstructor
@Service
@Qualifier(value = "StorageS3ServiceImpl")
public class StorageS3ServiceImpl implements StorageService {

  private final AmazonS3 amazonS3;

  @Value("${cloud.aws.s3.bucket}")
  private String bucketName;

  @Override
  public void upload(String imageName, InputStream inputStream, ObjectMetadata objectMetadata) {
    amazonS3.putObject(new PutObjectRequest(
        bucketName,
        imageName,
        inputStream,
        objectMetadata)
        .withCannedAcl(CannedAccessControlList.PublicRead)
    );
  }

  @Override
  public String getUrl(String imageName) {
    return amazonS3.getUrl(bucketName, imageName).toString();
  }
}
