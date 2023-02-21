package com.firefighter.aenitto.message.controller;

import static com.firefighter.aenitto.members.MemberFixture.*;
import static com.firefighter.aenitto.message.ImageFixture.*;
import static com.firefighter.aenitto.message.MessageFixture.*;
import static com.firefighter.aenitto.rooms.RoomFixture.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firefighter.aenitto.common.exception.GlobalExceptionHandler;
import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.messages.controller.MessageController;
import com.firefighter.aenitto.messages.controller.MessageControllerV2;
import com.firefighter.aenitto.messages.domain.Message;
import com.firefighter.aenitto.messages.dto.response.ReceivedMessagesResponse;
import com.firefighter.aenitto.messages.dto.response.SentMessagesResponse;
import com.firefighter.aenitto.messages.dto.response.version2.ReceivedMessagesResponseV2;
import com.firefighter.aenitto.messages.dto.response.version2.SentMessagesResponseV2;
import com.firefighter.aenitto.messages.service.MessageService;
import com.firefighter.aenitto.rooms.domain.MemberRoom;
import com.firefighter.aenitto.rooms.domain.Room;

@ExtendWith({RestDocumentationExtension.class, MockitoExtension.class})
@AutoConfigureRestDocs
@AutoConfigureMockMvc
public class MessageControllerV2Test {


	@InjectMocks
	private MessageControllerV2 target;

	@Autowired
	private MockMvc mockMvc;

	private ObjectMapper objectMapper;

	private MockMultipartFile image;
	private Member manitto;
	private Member manittee;
	private Room room;

	private MemberRoom myManitto;
	private MemberRoom myManittee;

	private Message message1;
	private Message message2;
	private Message message3;
	private Message message4;
	private Message message5;

	private List<Message> messages = new ArrayList<>();
	private List<Message> receivedMessages = new ArrayList<>();
	private List<Message> sentMessages = new ArrayList<>();

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
		room = roomFixture1();
		manittee = memberFixture2();
		manitto = memberFixture3();
		myManitto = memberRoomFixture1(manitto, room);
		myManittee = memberRoomFixture2(manittee, room);

		message1 = messageWithMisssionFixture1();

		messages.add(message1);
	}

	@DisplayName("보낸 메시지 가져오기V2 - 성공")
	@Test
	void get_sent_message_V2_success() throws Exception {
		//given
		final String uri = "/api/v2/rooms/{roomId}/messages-sent";
		Long roomId = 1L;
		when(messageService.getSentMessagesV2(any(Member.class), anyLong()))
			.thenReturn(SentMessagesResponseV2.of(messages, manittee));

		//when, then, docs
		mockMvc.perform(RestDocumentationRequestBuilders.get(uri, roomId)
				.header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.count").exists())
			.andExpect(jsonPath("$.messages[0].id").exists())
			.andDo(document("보낸 메시지 가져오기V2",
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
					fieldWithPath("messages[0].createdDate").description("메세지 생성 날짜"),
					fieldWithPath("messages[0].missionInfo").description("메세지의 미션 정보"),
					fieldWithPath("messages[0].missionInfo.id").description("메세지의 미션 id"),
					fieldWithPath("messages[0].missionInfo.content").description("메세지의 미션 내용"),
					fieldWithPath("manittee").description("내 마니띠"),
					fieldWithPath("manittee.id").description("마니띠 id"),
					fieldWithPath("manittee.nickname").description("내 마니띠 닉네임")
				)
			));
	}

	@DisplayName("받은 메시지 가져오기V2 - 성공")
	@Test
	void get_received_message_success() throws Exception {
		//given
		final String uri = "/api/v2/rooms/{roomId}/messages-received";
		Long roomId = 1L;
		when(messageService.getReceivedMessagesV2(any(Member.class), anyLong()))
			.thenReturn(ReceivedMessagesResponseV2.of(messages));

		//when, then, docs
		mockMvc.perform(RestDocumentationRequestBuilders.get(uri, roomId)
				.header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.count").exists())
			.andExpect(jsonPath("$.messages[0].id").exists())
			.andDo(document("받은 메시지 가져오기V2",
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
					fieldWithPath("messages[0].createdDate").description("메세지 생성 날짜"),
					fieldWithPath("messages[0].missionInfo").description("메세지의 미션 정보"),
					fieldWithPath("messages[0].missionInfo.id").description("메세지의 미션 id"),
					fieldWithPath("messages[0].missionInfo.content").description("메세지의 미션 내용")
				)
			));
		;
	}

}
