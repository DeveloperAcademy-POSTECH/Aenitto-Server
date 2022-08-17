package com.firefighter.aenitto.message.service;

import com.firefighter.aenitto.common.exception.member.MemberNotFoundException;
import com.firefighter.aenitto.common.exception.message.FileUploadException;
import com.firefighter.aenitto.common.exception.message.ImageExtensionNotFoundException;
import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.members.repository.MemberRepository;
import com.firefighter.aenitto.messages.domain.Message;
import com.firefighter.aenitto.messages.dto.request.SendMessageRequest;
import com.firefighter.aenitto.messages.repository.MessageRepository;
import com.firefighter.aenitto.messages.service.MessageServiceImpl;
import com.firefighter.aenitto.messages.service.StorageS3ServiceImpl;
import com.firefighter.aenitto.messages.service.StorageService;
import com.firefighter.aenitto.rooms.repository.RelationRepository;
import org.aspectj.lang.annotation.Before;
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
import java.util.Optional;
import java.util.UUID;

import static com.firefighter.aenitto.members.MemberFixture.memberFixture;
import static com.firefighter.aenitto.members.MemberFixture.memberFixture2;
import static com.firefighter.aenitto.message.ImageFixture.IMAGE;
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
    @Qualifier("StorageS3ServiceImpl")
    private StorageService storageS3Service;

    private Message message;

    private Member sender;
    private Member reciever;

    private MultipartFile image;


    @BeforeEach
    void setUp() {
        sender = memberFixture();
        reciever = memberFixture2();
        image = IMAGE;
    }


    @DisplayName("메세지 생성 - 실패 / 사진파일의 확장자명을 찾을 수 없음")
    @Test
    void create_message_fail_image_not_found_file_extension() throws Exception {
        //given
        doReturn(Optional.of(reciever)).when(memberRepository).findByMemberId(any(UUID.class));
        MockMultipartFile notExtensionFile = new MockMultipartFile("1", "1".getBytes());
        SendMessageRequest request = SendMessageRequest.builder()
                .recieverId(reciever.getId().toString()).build();

        //when, then
        assertThatExceptionOfType(ImageExtensionNotFoundException.class)
                .isThrownBy(() -> {
                    target.sendMessage(sender, request, notExtensionFile);
                });

        verify(memberRepository, times(1)).findByMemberId(reciever.getId());
    }

    //    @DisplayName("메세지 생성 - 실패 / 사진파일이 용량 초과")
//    @Test
//    void create_message_fail_user_not_exists() throws Exception{
//        //given
//
//    }
//
    @DisplayName("메세지 생성 - 실패 / 사진 파일 업로드 중 예외 발생")
    @Test
    void create_message_fail_user_not_exists() throws Exception {
        //given
        doReturn(Optional.of(reciever)).when(memberRepository).findByMemberId(any(UUID.class));
        SendMessageRequest request = SendMessageRequest.builder()
                .recieverId(reciever.getId().toString()).build();

        doThrow(FileUploadException.class).when(storageS3Service).upload(any(), any(), any());

        //when, then
        assertThatExceptionOfType(FileUploadException.class)
                .isThrownBy(() -> {
                    target.sendMessage(sender, request, image);
                });

        verify(memberRepository, times(1)).findByMemberId(reciever.getId());
    }



    @DisplayName("메세지 생성 - 실패 / 메시지를 보낼 수 없는 상대")
    @Test
    void create_message_fail_reciever_not_exists() throws Exception{
        //given
        doReturn(Optional.of(reciever)).when(memberRepository).findByMemberId(any(UUID.class));
        SendMessageRequest request = SendMessageRequest.builder()
                .recieverId(reciever.getId().toString()).build();

        //when, then
        assertThatExceptionOfType(MemberNotFoundException.class)
                .isThrownBy(() -> {
                    target.sendMessage(sender, request, image);
                });
    }

//    @DisplayName("메세지 생성 - 실패 / 참여하고 있지 않은 방입니다")
//    @Test
//    void create_message_fail_reciever_not_exists() throws Exception{
//        //given
//
//    }


//    @DisplayName("메세지 생성 - 성공 / 이미지만 저장")
//    @Test
//    void create_message_success() {
//        //given
//        doReturn(Optional.of(reciever)).when(memberRepository).findByMemberId(any(UUID.class));
//        SendMessageRequest request = SendMessageRequest.builder()
//                .recieverId(reciever.getId().toString()).messageContent("testContent").build();
//
//        //when
//        Long messageId = target.sendMessage(sender, request, image);
//
//        //then
//        assertThat(messageId).isEqualTo(1L);
//        verify(memberRepository, times(1)).findByMemberId(reciever.getId());
//        verify(messageRepository, times(1)).saveMessage(message);
//    }
}
