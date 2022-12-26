package com.firefighter.aenitto.support;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.io.InputStream;
import java.util.UUID;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.firefighter.aenitto.messages.service.StorageS3ServiceImpl;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class IntegrationTest {
	@Autowired
	protected EntityManager em;
	@Autowired
	protected ObjectMapper objectMapper;
	@MockBean
	protected StorageS3ServiceImpl storageS3Service;
	@Autowired
	WebApplicationContext webApplicationContext;
	protected MockMvc mockMvc;

	protected static final UUID MOCK_USER_ID = UUID.fromString("f383cdb3-a871-4410-b146-fb1f7b447b9e");
	protected static final String STORAGE_SAVED_IMG_URL = "sampleUrl";

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
			.apply(springSecurity())
			.alwaysDo(print())
			.addFilters(new CharacterEncodingFilter("UTF-8", true))
			.build();
	}

	@BeforeEach
	void stubS3StorageService() {
		when(storageS3Service.getUrl(anyString()))
			.thenReturn(STORAGE_SAVED_IMG_URL);
		doNothing()
			.when(storageS3Service).upload(anyString(), any(InputStream.class), any(ObjectMetadata.class));
	}

	protected void flushAndClear() {
		em.flush();
		em.clear();
	}

	protected MockMultipartFile createJsonFile(Object request) throws JsonProcessingException {
		return new MockMultipartFile(
			"request",
			"",
			MediaType.APPLICATION_JSON_VALUE,
			objectMapper.writeValueAsString(request).getBytes()
		);
	}
}

