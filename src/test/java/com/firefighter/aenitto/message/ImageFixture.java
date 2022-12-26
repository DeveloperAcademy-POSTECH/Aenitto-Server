package com.firefighter.aenitto.message;

import org.apache.http.entity.ContentType;
import org.springframework.mock.web.MockMultipartFile;

public class ImageFixture {
    public static MockMultipartFile IMAGE = new MockMultipartFile("image",
            "test.jpg",
            ContentType.IMAGE_JPEG.getMimeType(),
            "테스트파일".getBytes());
}
