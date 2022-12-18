package com.firefighter.aenitto.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.firefighter.aenitto.notification.dto.FcmMessage;
import com.firefighter.aenitto.rooms.repository.RelationRepository;

import org.junit.Ignore;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {
	@InjectMocks
	private FcmService fcmService;

	@Mock
	private ObjectMapper objectMapper;

	@DisplayName("FCM 메시지 만들기 - 성공")
	@Disabled
	@Test
	void make_message_success() throws JsonProcessingException {
		// given
		ObjectMapper realObjectMapper = new ObjectMapper();
		String targetToken = "token";
		String title = "title";
		String body = "body";
		FcmMessage fcmMessage = FcmMessage.builder().
			targetToken(targetToken).title(title).body(body).build();
		String fcmMessageString = realObjectMapper.writeValueAsString(fcmMessage);

		doReturn(fcmMessageString)
			.when(objectMapper).writeValueAsString(any());

		// when
		String result = ReflectionTestUtils.invokeMethod(fcmService, "makeMessage", targetToken, title, body);

		// then
		assertThat(result).isNotNull();
	}

	@DisplayName("FCM access token 받기 - 성공")
	@Disabled
	@Test
	void get_access_token_success() {

		// when
		String result = ReflectionTestUtils.invokeMethod(fcmService, "getAccessToken");

		// then
		assertThat(result).isNotNull();
	}

}
