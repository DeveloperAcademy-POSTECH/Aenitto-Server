package com.firefighter.aenitto.auth.client;

import com.firefighter.aenitto.auth.client.dto.ApplePublicKeyResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@ExtendWith(MockitoExtension.class)
public class ClientAppleTest {
    @InjectMocks
    private ClientApple target;

    private MockWebServer server;

    private WebClient webClient;

    @BeforeEach
    void setUp() {
        ReactorClientHttpConnector connector = new ReactorClientHttpConnector();
        server = new MockWebServer();
        webClient = WebClient
                .builder()
                .clientConnector(connector)
                .baseUrl(server.url("/").toString())
                .build();
    }

    @AfterEach
    void shutdown() throws IOException {
        server.shutdown();
    }


    //TODO: apple public key 가져오기 test code (2022.08.30) - 다온
//    @DisplayName("애플 퍼블릭 키 가져오기 / 실패")
//    @Test
//    void apple_token_validation_fail_not_participating() {
//        MockResponse mockResponse = new MockResponse()
//                .addHeader("Authorization", "someTokenValue")
//                .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
//                .setResponseCode(HttpStatus.OK.value())
//                .setBody("This is Response Body");
//
//        server.enqueue(mockResponse);
//
//        // when
//        ApplePublicKeyResponse response = target.getAppleAuthPublicKey();
//
//        //then
//        assertThat(response.getMatchedKeyBy("ES256","3UHT5POLK9")).isNotNull();
//    }
}
