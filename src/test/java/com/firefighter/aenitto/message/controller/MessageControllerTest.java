package com.firefighter.aenitto.message.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.firefighter.aenitto.common.exception.GlobalExceptionHandler;
import com.firefighter.aenitto.common.exception.member.MemberErrorCode;
import com.firefighter.aenitto.common.exception.member.MemberNotFoundException;
import com.firefighter.aenitto.common.exception.message.ImageExtensionNotFoundException;
import com.firefighter.aenitto.common.exception.message.MessageErrorCode;
import com.firefighter.aenitto.common.exception.message.NotManitteeException;
import com.firefighter.aenitto.common.exception.room.RoomErrorCode;
import com.firefighter.aenitto.common.exception.room.RoomNotParticipatingException;
import com.firefighter.aenitto.members.controller.MemberController;
import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.members.dto.request.ChangeNicknameRequest;
import com.firefighter.aenitto.members.service.MemberService;
import com.firefighter.aenitto.message.MessageFixture;
import com.firefighter.aenitto.message.dto.SendMessageRequestMultipartFile;
import com.firefighter.aenitto.messages.controller.MessageController;
import com.firefighter.aenitto.messages.dto.request.SendMessageRequest;
import com.firefighter.aenitto.messages.service.MessageService;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import static com.firefighter.aenitto.message.ImageFixture.IMAGE;
import static com.firefighter.aenitto.message.MessageFixture.messageFixture1;
import static com.firefighter.aenitto.message.dto.SendMessageRequestMultipartFile.requestMultipartFile;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith({RestDocumentationExtension.class, MockitoExtension.class})
@AutoConfigureRestDocs
@AutoConfigureMockMvc
public class MessageControllerTest {

    @InjectMocks
    private MessageController target;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private MockMultipartFile image;

    @Mock
    @Qualifier("memberServiceImpl")
    private MessageService messageService;

    @BeforeEach
    void init(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders.standaloneSetup(target)
                .setControllerAdvice(GlobalExceptionHandler.class)
                .apply(documentationConfiguration(restDocumentation))
                .build();

        objectMapper = new ObjectMapper();
        image = IMAGE;
    }

    @DisplayName("메세지 생성 - 실패 / 잘못된 사진일 경우")
    @Test
    void send_message_fail_wrong_image_file() throws Exception {
        //given
        final String uri = "/api/v1/rooms/{roomId}/messages";

        MockMultipartFile wrongImage = new MockMultipartFile("image",
                "testjpg",
                ContentType.IMAGE_JPEG.getMimeType(),
                "테스트파일".getBytes());

        doThrow(new ImageExtensionNotFoundException())
                .when(messageService)
                .sendMessage(any(Member.class), anyLong(), any(), any());

        //when, then, docs
        mockMvc.perform(
                        MockMvcRequestBuilders.multipart(uri, "1")
                                .file(wrongImage)
                                .file(requestMultipartFile())
                                .header(HttpHeaders.AUTHORIZATION, "Bearer testAccessToken")
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.message", MessageErrorCode.IMAGE_EXTENSION_NOT_FOUND.getMessage()).exists())
                .andExpect(jsonPath("$.status", MessageErrorCode.IMAGE_EXTENSION_NOT_FOUND.getStatus()).exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors").exists());
    }

    @DisplayName("메세지 생성 - 실패 / 참여하고 있지 않은 방")
    @Test
    void send_message_fail_not_participating_room() throws Exception {
        //given
        final String uri = "/api/v1/rooms/{roomId}/messages";

        doThrow(new RoomNotParticipatingException())
                .when(messageService)
                .sendMessage(any(Member.class), anyLong(), any(), any());

        //when, then, docs
        mockMvc.perform(
                        MockMvcRequestBuilders.multipart(uri, "1")
                                .file(requestMultipartFile())
                                .header(HttpHeaders.AUTHORIZATION, "Bearer testAccessToken")
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", RoomErrorCode.ROOM_NOT_PARTICIPATING.getMessage()).exists())
                .andExpect(jsonPath("$.status", RoomErrorCode.ROOM_NOT_PARTICIPATING.getStatus()).exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors").exists());

    }

    @DisplayName("메세지 생성 - 실패 / 마니띠가 아님 - 메시지를 보낼 수 없음")
    @Test
    void send_message_fail_not_manittee() throws Exception {
        //given
        final String uri = "/api/v1/rooms/{roomId}/messages";

        doThrow(new NotManitteeException())
                .when(messageService)
                .sendMessage(any(Member.class), anyLong(), any(), any());

        //when, then, docs
        mockMvc.perform(MockMvcRequestBuilders.multipart(uri, "1")
                        .file(requestMultipartFile())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer testAccessToken")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", MessageErrorCode.NOT_MY_MANITTEE.getMessage()).exists())
                .andExpect(jsonPath("$.status", MessageErrorCode.NOT_MY_MANITTEE.getStatus()).exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors").exists());

    }

    @DisplayName("메세지 생성 - 성공")
    @Test
    void send_message_success() throws Exception {
        //given
        final String uri = "/api/v1/rooms/{roomId}/messages";

        //when, then, docs
        mockMvc.perform(MockMvcRequestBuilders.multipart(uri, "1")
                        .file(image)
                        .file(requestMultipartFile())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer testAccessToken")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

    }

//    TODO: 이미지 용량 체크
//    @DisplayName("메세지 생성 - 실패 / 이미지 파일 용량 초과")
//    @Test
//    void send_message_fail_image_size() throws Exception {
//        //givente
//        final String uri = "/api/v1/rooms/{roomId}/messages";
//
//        byte[] bytes = new byte[1024 * 1024 * 10];
//        MockMultipartFile largeImage = new MockMultipartFile("image",
//                "test.jpg",
//                ContentType.IMAGE_JPEG.getMimeType(),
//                bytes
//        );
//
//        //when, then, docs
//        mockMvc.perform(MockMvcRequestBuilders.multipart(uri, "1")
//                        .file(largeImage)
//                        .file(requestMultipartFile())
//                        .header(HttpHeaders.AUTHORIZATION, "Bearer testAccessToken")
//                        .contentType(MediaType.MULTIPART_FORM_DATA))
//                .andDo(print())
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.status", 400).exists())
//                .andExpect(jsonPath("$.message", "최대 2MB의 파일크기를 초과하였습니다").exists());
//    }

    @DisplayName("메세지 생성 - 실패 / 제약 조건을 지키지 않은 경우")
    @Test
    void send_message_fail_did_not_keep_constraint() throws Exception {
        //given
        final String uri = "/api/v1/rooms/{roomId}/messages";
        SendMessageRequest request = SendMessageRequest.builder()
                .manitteeId("alkdf;l0p==k").messageContent("string").build();
        String requestJson = objectMapper.writeValueAsString(request);
        MockMultipartFile requestMultipartfile = new MockMultipartFile("sendMessageRequest", "sendMessageRequest", "application/json", requestJson.getBytes());

        //when, then, docs
        mockMvc.perform(MockMvcRequestBuilders.multipart(uri, "1")
                        .file(requestMultipartFile())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer testAccessToken")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", 400).exists())
                .andExpect(jsonPath("$.message", "입력 조건에 대한 예외입니다").exists())
                .andExpect(jsonPath("$.errors[0].field", "manitteeId").exists());

    }
}
