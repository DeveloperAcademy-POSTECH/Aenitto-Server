package com.firefighter.aenitto.message.integration;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static com.firefighter.aenitto.message.ImageFixture.IMAGE;
import static com.firefighter.aenitto.message.dto.SendMessageRequestMultipartFile.requestMultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.firefighter.aenitto.common.exception.room.RoomErrorCode;
import com.firefighter.aenitto.common.utils.SqlPath;
import com.firefighter.aenitto.messages.domain.Message;
import com.firefighter.aenitto.support.IntegrationTest;
import com.firefighter.aenitto.support.security.WithMockCustomMember;

@WithMockCustomMember
public class MessageIntegrationTest extends IntegrationTest {

	@DisplayName("메시지 보내기 -> 성공")
	@Sql("classpath:relation.sql")
	@Test
	void send_message() throws Exception {
		// given
		MockMultipartFile image = IMAGE;

		//TODO createJson 사용해서 바꾸기 - 다온

		// when, then
		mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/rooms/{roomId}/messages", 100L)
				.file(image)
				.file(requestMultipartFile())
				.contentType(MediaType.MULTIPART_FORM_DATA))
			.andExpect(status().isCreated())
			.andExpect(header().exists("Location"));
	}

	@DisplayName("메시지 보내기 (seperate) -> 성공")
	@Sql("classpath:relation.sql")
	@Test
	void send_separate_message_success() throws Exception {
		// given
		MockMultipartFile image = IMAGE;
		final String messageContent = "message";
		final String manitteeId = "b383cdb3-a871-4410-b147-fb1f7b447b9e";

		// when, then
		mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/rooms/{roomId}/messages-separate", 100L)
				.file(image)
				.file("manitteeId", manitteeId.getBytes(StandardCharsets.UTF_8))
				.file("messageContent", messageContent.getBytes(StandardCharsets.UTF_8))
				.contentType(MediaType.MULTIPART_FORM_DATA)
			)
			.andExpect(status().isCreated())
			.andExpect(header().exists(HttpHeaders.LOCATION));

		flushAndClear();

		Message findMessage = em.createQuery(
				"SELECT m"
					+ " FROM Message m"
				, Message.class)
			.getResultList().get(0);

		assertThat(findMessage.getSender().getId()).isEqualTo(MOCK_USER_ID);
		assertThat(findMessage.getReceiver().getId()).isEqualTo(UUID.fromString(manitteeId));
		assertThat(findMessage.didRead()).isFalse();
		assertThat(findMessage.getContent()).isEqualTo(messageContent);
		assertThat(findMessage.getImgUrl()).isEqualTo(STORAGE_SAVED_IMG_URL);
	}

	@DisplayName("보낸 메시지 가져오기 - 실패 / 참여하고 있는 방이 아님")
	@Sql("classpath:relation.sql")
	@Test
	void get_sent_messages_failure_room_not_participating() throws Exception {
		// when, then
		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/v1/rooms/{roomId}/messages-sent", 1L)
					.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.message", is(RoomErrorCode.ROOM_NOT_PARTICIPATING.getMessage())));
	}

	@DisplayName("보낸 메시지 가져오기 - 실패 / 마니또 존재 X")
	@Sql({
		SqlPath.MEMBER,
		SqlPath.ROOM_PROCESSING,
		SqlPath.MEMBER_ROOM
	})
	@Test
	void get_sent_messages_failure_manittee_not_exists() throws Exception {
		// when, then
		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/v1/rooms/{roomId}/messages-sent", 2L)
					.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message", is(RoomErrorCode.RELATION_NOT_FOUND.getMessage())));
	}

	@DisplayName("보낸 메시지 가져오기 - 성공")
	@Sql({
		SqlPath.MEMBER,
		SqlPath.ROOM_PROCESSING,
		SqlPath.MEMBER_ROOM,
		SqlPath.RELATION,
		SqlPath.SENT_MESSAGE
	})
	@Test
	void get_sent_messages_success() throws Exception {
		// when, then
		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/v1/rooms/{roomId}/messages-sent", 2L)
					.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.count").exists())
			.andExpect(jsonPath("$.messages[0].id").exists())
			.andExpect(jsonPath("$.messages[0].content").exists())
			.andExpect(jsonPath("$.messages[0].imageUrl").exists())
		;
	}

	@DisplayName("메세지 읽음으로 상태변경 - 실패 / 참여하고 있는 방이 아님")
	@Test
	void setStatusMessagesRead_failure_room_not_participating() throws Exception {
		// when, then
		mockMvc.perform(
				MockMvcRequestBuilders.patch("/api/v1/rooms/{roomId}/messages/status", 1L)
					.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.message", is(RoomErrorCode.ROOM_NOT_PARTICIPATING.getMessage())));
	}

	@DisplayName("메세지 읽음으로 상태변경 - 성공")
	@Sql({
		SqlPath.MEMBER,
		SqlPath.ROOM_PROCESSING,
		SqlPath.MEMBER_ROOM,
		SqlPath.RELATION,
		SqlPath.SENT_MESSAGE
	})
	@Test
	void setStatusMessagesRead_success() throws Exception {
		// when, then
		mockMvc.perform(
				MockMvcRequestBuilders.patch("/api/v1/rooms/{roomId}/messages/status", 2L)
					.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNoContent());
	}

	@DisplayName("받은 메시지 가져오기 - 실패 / 참여중인 방이 아님")
	@Test
	void get_received_messages_failure_room_not_participating() throws Exception {
		// when, then
		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/v1/rooms/{roomId}/messages-received", 1L)
					.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.message", is(RoomErrorCode.ROOM_NOT_PARTICIPATING.getMessage())));
	}

	@DisplayName("받은 메시지 가져오기 - 실패 / 마니또 존재 X")
	@Sql({
		SqlPath.MEMBER,
		SqlPath.ROOM_PROCESSING,
		SqlPath.MEMBER_ROOM
	})
	@Test
	void get_received_messages_failure_manittee_not_exists() throws Exception {
		// when, then
		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/v1/rooms/{roomId}/messages-received", 2L)
					.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message", is(RoomErrorCode.RELATION_NOT_FOUND.getMessage())));
	}

	@DisplayName("받은 메시지 가져오기 - 성공")
	@Sql({
		SqlPath.MEMBER,
		SqlPath.ROOM_PROCESSING,
		SqlPath.MEMBER_ROOM,
		SqlPath.RELATION,
		SqlPath.RECEIVED_MESSAGE
	})
	@Test
	void get_received_messages_success() throws Exception {
		// when, then
		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/v1/rooms/{roomId}/messages-received", 2L)
					.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.count").exists())
			.andExpect(jsonPath("$.messages[0].id").exists())
			.andExpect(jsonPath("$.messages[0].content").exists())
			.andExpect(jsonPath("$.messages[0].imageUrl").exists())
		;
	}

	@DisplayName("추억 가져오기 - 실패 / 참여중인 방이 아님")
	@Test
	void get_memories_failure_room_not_participating() throws Exception {
		// when, then
		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/v1/rooms/{roomId}/memories", 1L)
					.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.message", is(RoomErrorCode.ROOM_NOT_PARTICIPATING.getMessage())));
	}

	@DisplayName("받은 메시지 가져오기 - 실패 / 마니또 존재 X")
	@Sql({
		SqlPath.MEMBER,
		SqlPath.ROOM_PROCESSING,
		SqlPath.MEMBER_ROOM
	})
	@Test
	void get_memories_failure_manittee_not_exists() throws Exception {
		// when, then
		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/v1/rooms/{roomId}/memories", 2L)
					.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message", is(RoomErrorCode.RELATION_NOT_FOUND.getMessage())));
	}

	@DisplayName("추억 가져오기 - 성공")
	@Sql({
		SqlPath.MEMBER,
		SqlPath.ROOM_PROCESSING,
		SqlPath.MEMBER_ROOM,
		SqlPath.RELATION,
		SqlPath.MEMORIES
	})
	@Test
	void get_memories_success() throws Exception {
		// when, then
		mockMvc.perform(
				MockMvcRequestBuilders.get("/api/v1/rooms/{roomId}/memories", 2L)
					.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.memoriesWithManitto.member.nickname").exists())
			.andExpect(jsonPath("$.memoriesWithManitto.messages[0].id").exists())
			.andExpect(jsonPath("$.memoriesWithManitto.messages[1].id").exists())
			.andExpect(jsonPath("$.memoriesWithManitto.messages[2].id").exists())
			.andExpect(jsonPath("$.memoriesWithManitto.messages[3].id").exists())
			.andExpect(jsonPath("$.memoriesWithManittee.member.nickname").exists())
			.andExpect(jsonPath("$.memoriesWithManittee.messages[0].id").exists())
			.andExpect(jsonPath("$.memoriesWithManittee.messages[1].id").exists())
			.andExpect(jsonPath("$.memoriesWithManittee.messages[2].id").exists())
			.andExpect(jsonPath("$.memoriesWithManittee.messages[3].id").exists())

		;
	}
}
