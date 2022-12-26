package com.firefighter.aenitto.message.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static com.firefighter.aenitto.members.MemberFixture.*;
import static com.firefighter.aenitto.message.ImageFixture.IMAGE;
import static com.firefighter.aenitto.message.MessageFixture.*;
import static com.firefighter.aenitto.rooms.RoomFixture.*;
import static com.firefighter.aenitto.rooms.domain.RelationFixture.relationFixture;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import com.firefighter.aenitto.common.exception.message.FileUploadException;
import com.firefighter.aenitto.common.exception.message.ImageExtensionNotFoundException;
import com.firefighter.aenitto.common.exception.message.NotManitteeException;
import com.firefighter.aenitto.common.exception.room.RelationNotFoundException;
import com.firefighter.aenitto.common.exception.room.RoomNotParticipatingException;
import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.members.repository.MemberRepository;
import com.firefighter.aenitto.messages.domain.Message;
import com.firefighter.aenitto.messages.dto.request.SendMessageRequest;
import com.firefighter.aenitto.messages.dto.response.MemoriesResponse;
import com.firefighter.aenitto.messages.dto.response.ReceivedMessagesResponse;
import com.firefighter.aenitto.messages.dto.response.SentMessagesResponse;
import com.firefighter.aenitto.messages.repository.MessageRepository;
import com.firefighter.aenitto.messages.service.MessageServiceImpl;
import com.firefighter.aenitto.messages.service.StorageS3ServiceImpl;
import com.firefighter.aenitto.rooms.domain.MemberRoom;
import com.firefighter.aenitto.rooms.domain.Relation;
import com.firefighter.aenitto.rooms.domain.Room;
import com.firefighter.aenitto.rooms.repository.RelationRepository;
import com.firefighter.aenitto.rooms.repository.RoomRepository;

@ExtendWith(MockitoExtension.class)
public class MessageServiceImplTest {
	@InjectMocks
	MessageServiceImpl target;

	@Mock
	@Qualifier("memberRepositoryImpl")
	private MemberRepository memberRepository;

	@Mock
	@Qualifier("relationShipRepositoryImpl")
	private RelationRepository relationRepository;

	@Mock
	@Qualifier("messageRepositoryImpl")
	private MessageRepository messageRepository;

	@Mock
	@Qualifier("roomRepositoryImpl")
	private RoomRepository roomRepository;

	@Mock
	private StorageS3ServiceImpl storageS3Service;

	private Message message;
	private Message message1;
	private Message message2;
	private Message message3;
	private Message message4;
	private Message message5;

	private Member manitto;
	private Member manittee;

	private MemberRoom myManitto;
	private MemberRoom myManittee;

	private Member currentMember;

	private MemberRoom memberRoom;

	private Member notManittee;

	private Relation relation;

	private Room room;

	private MultipartFile image;

	List<Message> messages = new ArrayList<>();
	List<Message> messages2 = new ArrayList<>();

	@BeforeEach
	void setUp() {
		currentMember = memberFixture();
		manitto = memberFixture();
		manittee = memberFixture2();
		notManittee = memberFixture3();
		room = roomFixture1();
		relation = relationFixture(manitto, manittee, room);
		image = IMAGE;

		message = messageFixture1();
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

	@DisplayName("메세지 생성 - 실패 / 사진파일의 확장자명을 찾을 수 없음")
	@Test
	void create_message_fail_image_not_found_file_extension() throws Exception {
		//given
		Long roomId = 1L;
		doReturn(Optional.of(relation)).when(relationRepository)
			.findByRoomIdAndManittoId(anyLong(), any(UUID.class));
		SendMessageRequest request = SendMessageRequest.builder()
			.manitteeId(manittee.getId().toString()).build();
		MockMultipartFile notExtensionFile = new MockMultipartFile("1", "1".getBytes());

		//when, then
		assertThatExceptionOfType(ImageExtensionNotFoundException.class)
			.isThrownBy(() -> {
				target.sendMessage(manitto, roomId, request, notExtensionFile);
			});
	}

	@DisplayName("메세지 생성 - 실패 / 참여하고 있지 않은 방")
	@Test
	void create_message_fail_user_not_participating() throws Exception {
		//given
		Long roomId = 1L;
		doReturn(Optional.empty()).when(relationRepository)
			.findByRoomIdAndManittoId(anyLong(), any(UUID.class));
		SendMessageRequest request = SendMessageRequest.builder()
			.manitteeId(manittee.getId().toString()).build();

		//when, then
		assertThatExceptionOfType(RoomNotParticipatingException.class)
			.isThrownBy(() -> {
				target.sendMessage(manitto, roomId, request, image);
			});

		verify(relationRepository, times(1))
			.findByRoomIdAndManittoId(roomId, manitto.getId());
	}

	@DisplayName("메세지 생성 - 실패 / 사진 파일 업로드 중 예외 발생")
	@Test
	void create_message_fail_image_upload_exception() throws Exception {
		//given
		Long roomId = 1L;
		doReturn(Optional.of(relation)).when(relationRepository)
			.findByRoomIdAndManittoId(anyLong(), any(UUID.class));
		SendMessageRequest request = SendMessageRequest.builder()
			.manitteeId(manittee.getId().toString()).build();

		doThrow(FileUploadException.class).when(storageS3Service).upload(any(), any(), any());

		//when, then
		assertThatExceptionOfType(FileUploadException.class)
			.isThrownBy(() -> {
				target.sendMessage(manitto, roomId, request, image);
			});

		verify(relationRepository, times(1))
			.findByRoomIdAndManittoId(roomId, manitto.getId());
	}

	@DisplayName("메세지 생성 - 실패 / 마니띠가 아님 - 메시지를 보낼 수 없음")
	@Test
	void create_message_fail_not_manittee_exception() throws Exception {
		//given
		Long roomId = 1L;
		doReturn(Optional.of(relation)).when(relationRepository)
			.findByRoomIdAndManittoId(anyLong(), any(UUID.class));
		SendMessageRequest request = SendMessageRequest.builder()
			.manitteeId(UUID.randomUUID().toString()).build();

		//when, then
		assertThatExceptionOfType(NotManitteeException.class)
			.isThrownBy(() -> {
				target.sendMessage(manitto, roomId, request, image);
			});
		verify(relationRepository, times(1))
			.findByRoomIdAndManittoId(roomId, manitto.getId());
	}

	@DisplayName("메세지 생성 - 성공")
	@Test
	void create_message_success() {
		//given
		Long roomId = 1L;
		doReturn(Optional.of(relation)).when(relationRepository)
			.findByRoomIdAndManittoId(anyLong(), any(UUID.class));
		doReturn(message).when(messageRepository)
			.saveMessage(any(Message.class));

		SendMessageRequest request = SendMessageRequest.builder()
			.messageContent("message")
			.manitteeId(manittee.getId().toString()).build();

		//when
		Long messageId = target.sendMessage(manitto, roomId, request, image);

		//then
		verify(relationRepository, times(1))
			.findByRoomIdAndManittoId(roomId, manitto.getId());
		verify(messageRepository, times(1)).saveMessage(any(Message.class));
	}

	@DisplayName("보낸 메시지 가져오기 - 실패 / 참여하고 있지 않은 방")
	@Test
	void getSentMessages_failure_not_participating_room() {
		//given
		doReturn(Optional.empty()).when(roomRepository)
			.findMemberRoomById(any(UUID.class), anyLong());

		//when
		assertThatExceptionOfType(RoomNotParticipatingException.class)
			.isThrownBy(() -> {
				target.getSentMessages(manitto, room.getId());
			});
		verify(roomRepository, times(1))
			.findMemberRoomById(manitto.getId(), room.getId());
	}

	@DisplayName("보낸 메시지 가져오기 - 실패 / 마니띠가 존재하지 않습니다")
	@Test
	void getSentMessages_failure_manittee_not_exists() {
		//given
		memberRoom = memberRoomFixture1(manitto, room);
		doReturn(Optional.ofNullable(memberRoom)).when(roomRepository)
			.findMemberRoomById(manitto.getId(), room.getId());
		doReturn(Optional.empty()).when(relationRepository)
			.findByRoomIdAndManittoId(anyLong(), any(UUID.class));

		//when, then
		assertThatExceptionOfType(RelationNotFoundException.class)
			.isThrownBy(() -> {
				target.getSentMessages(manitto, room.getId());
			});
		verify(roomRepository, times(1))
			.findMemberRoomById(manitto.getId(), room.getId());
		verify(relationRepository, times(1))
			.findByRoomIdAndManittoId(room.getId(), manitto.getId());

	}

	@DisplayName("보낸 메시지 가져오기 - 성공")
	@Test
	void getSentMessages_success() {
		//given
		memberRoom = memberRoomFixture1(manitto, room);
		doReturn(Optional.ofNullable(memberRoom)).when(roomRepository)
			.findMemberRoomById(manitto.getId(), room.getId());
		doReturn(Optional.ofNullable(relation)).when(relationRepository)
			.findByRoomIdAndManittoId(room.getId(), manitto.getId());
		doReturn(messages).when(messageRepository)
			.getSentMessages(manitto.getId(), room.getId());

		//when
		SentMessagesResponse response = target.getSentMessages(manitto, room.getId());

		//then
		assertThat(response.getCount()).isEqualTo(5);
		assertThat(response.getMessages().size()).isEqualTo(5);
		assertThat(response.getMessages().get(0).getId()).isEqualTo(1L);

		verify(roomRepository, times(1))
			.findMemberRoomById(manitto.getId(), room.getId());
		verify(relationRepository, times(1))
			.findByRoomIdAndManittoId(room.getId(), manitto.getId());
		verify(messageRepository, times(1))
			.getSentMessages(manitto.getId(), room.getId());
	}

	@DisplayName("읽은 메세지 상태 변경 - 실패 / 참여하고 있지 않은 방")
	@Test
	void setReadMessagesStatus_failure_not_participating_room() {
		//given
		doReturn(Optional.empty()).when(roomRepository)
			.findMemberRoomById(any(UUID.class), anyLong());

		//when
		assertThatExceptionOfType(RoomNotParticipatingException.class)
			.isThrownBy(() -> {
				target.setReadMessagesStatus(manitto, room.getId());
			});
		verify(roomRepository, times(1))
			.findMemberRoomById(manitto.getId(), room.getId());
	}

	@DisplayName("읽은 메세지 상태 변경 - 성공")
	@Test
	void setReadMessagesStatus_success() {
		//given
		memberRoom = memberRoomFixture1(manittee, room);
		doReturn(Optional.ofNullable(memberRoom)).when(roomRepository)
			.findMemberRoomById(manittee.getId(), room.getId());
		doReturn(messages).when(messageRepository)
			.findMessagesByReceiverIdAndRoomIdAndStatus(manittee.getId(), room.getId(), false);

		//when
		target.setReadMessagesStatus(manittee, room.getId());

		//then
		assertThat(messages.get(3).isRead()).isEqualTo(true);
		assertThat(messages.get(4).isRead()).isEqualTo(true);

		verify(roomRepository, times(1))
			.findMemberRoomById(manittee.getId(), room.getId());
		verify(messageRepository, times(1))
			.findMessagesByReceiverIdAndRoomIdAndStatus(manittee.getId(), room.getId(), false);
	}

	@DisplayName("받은 메시지 가져오기 - 실패 / 참여하고 있지 않은 방")
	@Test
	void getReceivedMessages_failure_not_participating_room() {
		//given
		doReturn(Optional.empty()).when(roomRepository)
			.findMemberRoomById(any(UUID.class), anyLong());

		//when
		assertThatExceptionOfType(RoomNotParticipatingException.class)
			.isThrownBy(() -> {
				target.getReceivedMessages(manittee, room.getId());
			});
		verify(roomRepository, times(1))
			.findMemberRoomById(manittee.getId(), room.getId());
	}

	@DisplayName("받은 메시지 가져오기 - 실패 / 마니또가 존재하지 않습니다")
	@Test
	void getReceivedMessages_failure_manitto_not_exists() {
		//given
		memberRoom = memberRoomFixture1(manittee, room);
		doReturn(Optional.ofNullable(memberRoom)).when(roomRepository)
			.findMemberRoomById(manittee.getId(), room.getId());
		doReturn(Optional.empty()).when(relationRepository)
			.findByRoomIdAndManitteeId(anyLong(), any(UUID.class));

		//when, then
		assertThatExceptionOfType(RelationNotFoundException.class)
			.isThrownBy(() -> {
				target.getReceivedMessages(manittee, room.getId());
			});
		verify(roomRepository, times(1))
			.findMemberRoomById(manittee.getId(), room.getId());
		verify(relationRepository, times(1))
			.findByRoomIdAndManitteeId(room.getId(), manittee.getId());

	}

	@DisplayName("받은 메시지 가져오기 - 성공")
	@Test
	void getReceivedMessages_success() {
		//given
		memberRoom = memberRoomFixture1(manittee, room);
		doReturn(Optional.ofNullable(memberRoom)).when(roomRepository)
			.findMemberRoomById(manittee.getId(), room.getId());
		doReturn(Optional.ofNullable(relation)).when(relationRepository)
			.findByRoomIdAndManitteeId(room.getId(), manittee.getId());
		doReturn(messages).when(messageRepository)
			.getReceivedMessages(manittee.getId(), room.getId());

		//when
		ReceivedMessagesResponse response = target.getReceivedMessages(manittee, room.getId());

		//then
		assertThat(response.getCount()).isEqualTo(5);
		assertThat(response.getMessages().size()).isEqualTo(5);
		assertThat(response.getMessages().get(0).getId()).isEqualTo(1L);

		verify(roomRepository, times(1))
			.findMemberRoomById(manittee.getId(), room.getId());
		verify(relationRepository, times(1))
			.findByRoomIdAndManitteeId(room.getId(), manittee.getId());
		verify(messageRepository, times(1))
			.getReceivedMessages(manittee.getId(), room.getId());
	}

	@DisplayName("추억 가져오기 - 실패 / 참여하고 있지 않은 방")
	@Test
	void getMemories_failure_not_participating_room() {
		//given
		doReturn(Optional.empty()).when(roomRepository)
			.findMemberRoomById(any(UUID.class), anyLong());

		//when
		assertThatExceptionOfType(RoomNotParticipatingException.class)
			.isThrownBy(() -> {
				target.getMemories(currentMember, room.getId());
			});
		verify(roomRepository, times(1))
			.findMemberRoomById(currentMember.getId(), room.getId());
	}

	@DisplayName("추억 가져오기 - 실패 / 마니또가 존재하지 않습니다")
	@Test
	void getMemories_failure_manitto_not_exists() {
		//given
		memberRoom = memberRoomFixture1(currentMember, room);
		doReturn(Optional.ofNullable(memberRoom)).when(roomRepository)
			.findMemberRoomById(currentMember.getId(), room.getId());
		doReturn(Optional.empty()).when(relationRepository)
			.findByRoomIdAndManitteeId(anyLong(), any(UUID.class));

		//when, then
		assertThatExceptionOfType(RelationNotFoundException.class)
			.isThrownBy(() -> {
				target.getMemories(currentMember, room.getId());
			});
		verify(roomRepository, times(1))
			.findMemberRoomById(currentMember.getId(), room.getId());
		verify(relationRepository, times(1))
			.findByRoomIdAndManitteeId(room.getId(), currentMember.getId());

	}

	@DisplayName("추억 가져오기 - 성공")
	@Test
	void getMemories_success() {
		//given
		List<Message> tempMessage = new ArrayList<>();
		tempMessage.add(message1);
		tempMessage.add(message2);
		myManittee = memberRoomFixture1(manitto, room);
		myManitto = memberRoomFixture2(manittee, room);

		memberRoom = memberRoomFixture1(currentMember, room);
		doReturn(Optional.ofNullable(memberRoom)).when(roomRepository)
			.findMemberRoomById(currentMember.getId(), room.getId());
		doReturn(Optional.ofNullable(relation)).when(relationRepository)
			.findByRoomIdAndManitteeId(room.getId(), currentMember.getId());
		doReturn(Optional.ofNullable(relation)).when(relationRepository)
			.findByRoomIdAndManittoId(room.getId(), currentMember.getId());
		doReturn(Optional.ofNullable(myManitto)).when(roomRepository)
			.findMemberRoomById(manitto.getId(), room.getId());
		doReturn(Optional.ofNullable(myManittee)).when(roomRepository)
			.findMemberRoomById(manittee.getId(), room.getId());
		doReturn(tempMessage).when(messageRepository)
			.getTwoRandomContentReceivedMessages(currentMember.getId(), room.getId());
		doReturn(tempMessage).when(messageRepository)
			.getTwoRandomImageReceivedMessages(currentMember.getId(), room.getId());
		doReturn(tempMessage).when(messageRepository)
			.getTwoRandomContentSentMessages(currentMember.getId(), room.getId());
		doReturn(tempMessage).when(messageRepository)
			.getTwoRandomImageSentMessages(currentMember.getId(), room.getId());

		//when
		MemoriesResponse response = target.getMemories(currentMember, room.getId());

		//then
		assertThat(response.getMemoriesWithManittee().getMessages().size()).isEqualTo(4);
		assertThat(response.getMemoriesWithManitto().getMessages().size()).isEqualTo(4);
		assertThat(response.getMemoriesWithManittee().getMember().getNickname()).isNotNull();
		assertThat(response.getMemoriesWithManitto().getMember().getNickname()).isNotNull();
	}

	@DisplayName("throwIfIdNotIdentical - 실패 (UUID 가 서로 같지 않음)")
	@Test
	void throwIfIdNotIdentical_fail_not_identical() {
		// given
		final String METHOD_NAME = "throwIfIdNotIdentical";
		UUID uuid1 = UUID.randomUUID();
		UUID uuid2 = UUID.randomUUID();

		// when, then
		assertThatThrownBy(() -> {
			ReflectionTestUtils.invokeMethod(target, METHOD_NAME, uuid1, uuid2);
		})
			.isInstanceOf(NotManitteeException.class);
	}

	@DisplayName("throwIfIdNotIdentical - 성공")
	@Test
	void throwIfIdNotIdentical_success() {
		// given
		final String METHOD_NAME = "throwIfIdNotIdentical";
		UUID uuid = UUID.randomUUID();

		// when, then
		assertThatNoException()
			.isThrownBy(() -> {
				ReflectionTestUtils.invokeMethod(target, METHOD_NAME, uuid, uuid);
			});
	}
}
