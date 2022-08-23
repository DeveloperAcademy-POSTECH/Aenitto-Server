package com.firefighter.aenitto.message.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firefighter.aenitto.common.exception.GlobalExceptionHandler;
import com.firefighter.aenitto.common.exception.message.ImageExtensionNotFoundException;
import com.firefighter.aenitto.common.exception.message.MessageErrorCode;
import com.firefighter.aenitto.common.exception.message.NotManitteeException;
import com.firefighter.aenitto.common.exception.room.RelationNotFoundException;
import com.firefighter.aenitto.common.exception.room.RoomErrorCode;
import com.firefighter.aenitto.common.exception.room.RoomNotParticipatingException;
import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.messages.controller.MessageController;
import com.firefighter.aenitto.messages.domain.Message;
import com.firefighter.aenitto.messages.dto.request.SendMessageRequest;
import com.firefighter.aenitto.messages.dto.response.SentMessagesResponse;
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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static com.firefighter.aenitto.members.MemberFixture.memberFixture2;
import static com.firefighter.aenitto.message.ImageFixture.IMAGE;
import static com.firefighter.aenitto.message.MessageFixture.*;
import static com.firefighter.aenitto.message.dto.SendMessageRequestMultipartFile.requestMultipartFile;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
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
    private Member manitto;
    private Member manittee;

    private Message message1;
    private Message message2;
    private Message message3;
    private Message message4;
    private Message message5;

    private List<Message> messages = new ArrayList<>();

    private final String ACCESS_TOKEN = "Bearer testAccessToken";

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
        manittee = memberFixture2();

        message1 = messageFixture1();
        message2 = messageFixture2();
        message3 = messageFixture3();
        message4 = messageFixture4();
        message5 = messageFixture5();

        messages.add(message1);
        messages.add(message2);
        messages.add(message3);
        messages.add(message4);
        messages.add(message5);
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
                                .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
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
                                .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
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
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", MessageErrorCode.NOT_MY_MANITTEE.getMessage()).exists())
                .andExpect(jsonPath("$.status", MessageErrorCode.NOT_MY_MANITTEE.getStatus()).exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors").exists());

    }

//     TODO: 문서화 진행 - 다온
    @DisplayName("메세지 생성 - 성공")
    @Test
    void send_message_success() throws Exception {
        //given
        final String uri = "/api/v1/rooms/{roomId}/messages";

        //when, then, docs
        mockMvc.perform(MockMvcRequestBuilders.multipart(uri, "1")
                        .file(image)
                        .file(requestMultipartFile())
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

    }

//    TODO: 이미지 용량 체크 테스트 만들기 - 다온
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
                        .file(requestMultipartfile)
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", 400).exists())
                .andExpect(jsonPath("$.message", "입력 조건에 대한 예외입니다").exists())
                .andExpect(jsonPath("$.errors[0].field", "manitteeId").exists());

    }

    @DisplayName("보낸 메시지 가져오기 - 참여중인 방이 아님")
    @Test
    void get_sent_message_failure_not_participating_room() throws Exception {
        //given
        final String uri = "/api/v1/rooms/{roomId}/messages-sent";
        doThrow(new RoomNotParticipatingException())
                .when(messageService)
                .getSentMessages(any(Member.class), anyLong());

        //when, then, docs
        mockMvc.perform(MockMvcRequestBuilders.get(uri, "4")
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", RoomErrorCode.ROOM_NOT_PARTICIPATING.getMessage()).exists())
                .andExpect(jsonPath("$.status", RoomErrorCode.ROOM_NOT_PARTICIPATING.getStatus()).exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors").exists());
    }

    @DisplayName("보낸 메시지 가져오기 - 마니띠가 존재하지 않음")
    @Test
    void get_sent_message_failure_relation_not_found() throws Exception {
        //given
        final String uri = "/api/v1/rooms/{roomId}/messages-sent";
        doThrow(new RelationNotFoundException())
                .when(messageService)
                .getSentMessages(any(Member.class), anyLong());

        //when, then, docs
        mockMvc.perform(MockMvcRequestBuilders.get(uri, "1")
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", RoomErrorCode.RELATION_NOT_FOUND.getMessage()).exists())
                .andExpect(jsonPath("$.status", RoomErrorCode.RELATION_NOT_FOUND.getStatus()).exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors").exists());
    }

    @DisplayName("보낸 메시지 가져오기 - 성공")
    @Test
    void get_sent_message_success() throws Exception {
        //given
        final String uri = "/api/v1/rooms/{roomId}/messages-sent";
        Long roomId = 1L;
        when(messageService.getSentMessages(any(Member.class), anyLong()))
                .thenReturn(SentMessagesResponse.of(messages, manittee));

        //when, then, docs
        mockMvc.perform(RestDocumentationRequestBuilders.get(uri, roomId)
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").exists())
                .andExpect(jsonPath("$.messages[0].id").exists())
                .andDo(document("보낸 메시지 가져오기",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("roomId").description("방 id")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("유저 인증 토큰")
                        ),
                        responseFields(
                                fieldWithPath("count").description("총 메시지 수"),
                                fieldWithPath("messages").description("보낸 메시지들"),
                                fieldWithPath("messages[0].id").description("메세지 id"),
                                fieldWithPath("messages[0].content").description("메세지 내용"),
                                fieldWithPath("messages[0].imageUrl").description("메세지에 들어간 사진"),
                                fieldWithPath("manittee").description("내 마니띠"),
                                fieldWithPath("manittee.id").description("마니띠 id"),
                                fieldWithPath("manittee.nickname").description("내 마니띠 닉네임")
                        )
                ));
        ;
    }

    @DisplayName("보낸 메시지 가져오기 - 실패 / 참여중인 방이 아님")
    @Test
    void setReadMessagesStatus_failure_not_participating_room() throws Exception {
        //given
        final String uri = "/api/v1/rooms/{roomId}/messages/status";
        doThrow(new RoomNotParticipatingException())
                .when(messageService)
                .setReadMessagesStatus(any(Member.class), anyLong());

        //when, then, docs
        mockMvc.perform(MockMvcRequestBuilders.patch(uri, "4")
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", RoomErrorCode.ROOM_NOT_PARTICIPATING.getMessage()).exists())
                .andExpect(jsonPath("$.status", RoomErrorCode.ROOM_NOT_PARTICIPATING.getStatus()).exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors").exists());
    }

    @DisplayName("보낸 메시지 가져오기 - 성공")
    @Test
    void setReadMessagesStatus_success() throws Exception {
        //given
        final String uri = "/api/v1/rooms/{roomId}/messages/status";

        //when, then, docs
        mockMvc.perform(MockMvcRequestBuilders.patch(uri, "4")
                        .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}
