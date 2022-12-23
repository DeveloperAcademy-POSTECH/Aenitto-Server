package com.firefighter.aenitto.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.firefighter.aenitto.common.exception.notification.FailedSendingNotificationException;
import com.firefighter.aenitto.notification.dto.FcmMessage;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.messaging.FirebaseMessaging;

import lombok.RequiredArgsConstructor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Qualifier("fcmNotificationService")
public class FcmService implements NotificationService {
	@Value("${fcm.key.path}")
	private String API_URL;
	private final ObjectMapper objectMapper;

	public void sendMessage(String targetToken, String title, String body) {
		try {
            String message = makeMessage(targetToken, title, body);

            OkHttpClient okHttpClient = new OkHttpClient();
            RequestBody requestBody = RequestBody.create(message,
                MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                .url(API_URL)
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION,
                    "Bearer " + getAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                .build();
			Response response = okHttpClient.newCall(request).execute();

		} catch (IOException e) {
			System.out.println(e.getMessage());
            throw new FailedSendingNotificationException();
        }

	}

	private String makeMessage(String targetToken, String title, String body) throws JsonProcessingException {

		FcmMessage fcmMessage = FcmMessage.builder()
			.title(title).body(body).targetToken(targetToken).build();
		return objectMapper.writeValueAsString(fcmMessage);
	}

	private String getAccessToken() throws IOException {
		String firebaseConfigPath = "firebase_service_key.json";

		GoogleCredentials googleCredentials = GoogleCredentials
			.fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
			.createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
		googleCredentials.refreshIfExpired();

		return googleCredentials.getAccessToken().getTokenValue();
	}
}
