package com.firefighter.aenitto.messages.service;

import com.amazonaws.services.s3.model.ObjectMetadata;

import java.io.InputStream;

public interface StorageService {
    public void upload(String imageName, InputStream inputStream, ObjectMetadata objectMetadata);
    public String getUrl(String imageName);
}
