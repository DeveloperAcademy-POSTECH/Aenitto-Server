package com.firefighter.aenitto.messages.service;

import java.io.InputStream;

import com.amazonaws.services.s3.model.ObjectMetadata;

public interface StorageService {
	public void upload(String imageName, InputStream inputStream, ObjectMetadata objectMetadata);

	public String getUrl(String imageName);
}
