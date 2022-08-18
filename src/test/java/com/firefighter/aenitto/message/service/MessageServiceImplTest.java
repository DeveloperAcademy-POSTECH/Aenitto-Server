package com.firefighter.aenitto.message.service;

import com.firefighter.aenitto.common.exception.message.FileUploadException;
import com.firefighter.aenitto.common.exception.message.ImageExtensionNotFoundException;
import com.firefighter.aenitto.common.exception.message.NotManitteeException;
import com.firefighter.aenitto.common.exception.room.RoomNotParticipatingException;
import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.members.repository.MemberRepository;
import com.firefighter.aenitto.messages.domain.Message;
import com.firefighter.aenitto.messages.dto.request.SendMessageRequest;
import com.firefighter.aenitto.messages.repository.MessageRepository;
import com.firefighter.aenitto.messages.service.MessageServiceImpl;
import com.firefighter.aenitto.messages.service.StorageS3ServiceImpl;
import com.firefighter.aenitto.rooms.domain.Relation;
import com.firefighter.aenitto.rooms.domain.Room;
import com.firefighter.aenitto.rooms.repository.RelationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.nio.channels.MulticastChannel;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.firefighter.aenitto.members.MemberFixture.*;
import static com.firefighter.aenitto.message.ImageFixture.IMAGE;
import static com.firefighter.aenitto.message.MessageFixture.messageFixture1;
import static com.firefighter.aenitto.rooms.RoomFixture.roomFixture1;
import static com.firefighter.aenitto.rooms.domain.RelationFixture.relationFixture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    private StorageS3ServiceImpl storageS3Service;

    private Message message;

    private Member manitto;
    private Member manittee;

    private Member notManittee;

    private Relation relation;

    private Room room;

    private MultipartFile image;


    @BeforeEach
    void setUp() {
        manitto = memberFixture();
        manittee = memberFixture2();
        notManittee = memberFixture3();
        room = roomFixture1();
        relation = relationFixture(manitto, manittee, room);
        image = IMAGE;
        message = messageFixture1();
    }


    @DisplayName("메세지 생성 - 실패 / 사진파일의 확장자명을 찾을 수 없음")
    @Test
    void create_message_fail_image_not_found_file_extension() throws Exception {
        //given
        Long roomId = 1L;
        doReturn(Optional.of(relation)).when(relationRepository)
                .findByRoomIdAndMemberId(anyLong(), any(UUID.class));
        SendMessageRequest request = SendMessageRequest.builder()
                .recieverId(manittee.getId().toString()).build();
        MockMultipartFile notExtensionFile = new MockMultipartFile("1", "1".getBytes());

        //when, then
        assertThatExceptionOfType(ImageExtensionNotFoundException.class)
                .isThrownBy(() -> {
                    target.sendMessage(manitto, roomId, request, notExtensionFile);
                });

        verify(relationRepository, times(1))
                .findByRoomIdAndMemberId(roomId, manitto.getId());
    }

    @DisplayName("메세지 생성 - 실패 / 참여하고 있지 않은 방")
    @Test
    void create_message_fail_user_not_participating() throws Exception{
        //given
        Long roomId = 1L;
        doReturn(Optional.empty()).when(relationRepository)
                .findByRoomIdAndMemberId(anyLong(), any(UUID.class));
        SendMessageRequest request = SendMessageRequest.builder()
                .recieverId(manittee.getId().toString()).build();

        //when, then
        assertThatExceptionOfType(RoomNotParticipatingException.class)
                .isThrownBy(() -> {
                    target.sendMessage(manitto, roomId, request, image);
                });

        verify(relationRepository, times(1))
                .findByRoomIdAndMemberId(roomId, manitto.getId());
    }

    @DisplayName("메세지 생성 - 실패 / 사진 파일 업로드 중 예외 발생")
    @Test
    void create_message_fail_image_upload_exception() throws Exception {
        //given
        Long roomId = 1L;
        doReturn(Optional.of(relation)).when(relationRepository)
                .findByRoomIdAndMemberId(anyLong(), any(UUID.class));
        SendMessageRequest request = SendMessageRequest.builder()
                .recieverId(manittee.getId().toString()).build();

        doThrow(FileUploadException.class).when(storageS3Service).upload(any(), any(), any());

        //when, then
        assertThatExceptionOfType(FileUploadException.class)
                .isThrownBy(() -> {
                    target.sendMessage(manitto, roomId, request, image);
                });

        verify(relationRepository, times(1))
                .findByRoomIdAndMemberId(roomId, manitto.getId());
    }


    @DisplayName("메세지 생성 - 실패 / 마니띠가 아님 - 메시지를 보낼 수 없음")
    @Test
    void create_message_fail_not_manittee_exception() throws Exception {
        //given
        Long roomId = 1L;
        doReturn(Optional.of(relation)).when(relationRepository)
                .findByRoomIdAndMemberId(anyLong(), any(UUID.class));
        SendMessageRequest request = SendMessageRequest.builder()
                .recieverId(UUID.randomUUID().toString()).build();

        //when, then
        assertThatExceptionOfType(NotManitteeException.class)
                .isThrownBy(() -> {
            target.sendMessage(manitto, roomId, request, image);
        });
        verify(relationRepository, times(1))
                .findByRoomIdAndMemberId(roomId, manitto.getId());
    }


    @DisplayName("메세지 생성 - 성공 / 이미지만 저장")
    @Test
    void create_message_success() {
        //given
        Long roomId = 1L;
        doReturn(Optional.of(relation)).when(relationRepository)
                .findByRoomIdAndMemberId(anyLong(), any(UUID.class));
        doReturn(message).when(messageRepository)
                .saveMessage(any(Message.class));

        SendMessageRequest request = SendMessageRequest.builder()
                .messageContent("message")
                .recieverId(manittee.getId().toString()).build();

        //when
        Long messageId = target.sendMessage(manitto, roomId, request, image);

        //then
        verify(relationRepository, times(1))
                .findByRoomIdAndMemberId(roomId, manitto.getId());
        verify(messageRepository, times(1)).saveMessage(any(Message.class));
    }
}
