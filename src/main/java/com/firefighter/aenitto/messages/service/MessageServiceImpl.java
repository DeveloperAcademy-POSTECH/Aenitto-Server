package com.firefighter.aenitto.messages.service;

import com.amazonaws.services.s3.model.ObjectMetadata;
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
import com.firefighter.aenitto.rooms.domain.MemberRoom;
import com.firefighter.aenitto.rooms.domain.Relation;
import com.firefighter.aenitto.rooms.repository.RelationRepository;
import com.firefighter.aenitto.rooms.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageServiceImpl implements MessageService {

    @Qualifier("memberRepositoryImpl")
    private final MemberRepository memberRepository;

    @Qualifier("relationRepositoryImpl")
    private final RelationRepository relationRepository;

    @Qualifier("messageRepositoryImpl")
    private final MessageRepository messageRepository;

    @Qualifier("roomRepositoryImpl")
    private final RoomRepository roomRepository;

    @Qualifier("StorageS3ServiceImpl")
    private final StorageService storageService;

    @Override
    @Transactional
    public long sendMessageSeparate(Member currentMember, Long roomId, String manitteeId,
                                String messageContent, MultipartFile image) {

        Relation relation = relationRepository.findByRoomIdAndManittoId(roomId, currentMember.getId())
                .orElseThrow(RoomNotParticipatingException::new);

        if (!Objects.equals(relation.getManittee().getId(), UUID.fromString(manitteeId))) {
            throw new NotManitteeException();
        }

        // TODO: 메시지 생성 메서드
        Message message = Message.builder().content(messageContent).build();
        message.sendMessage(relation.getManitto(), relation.getManittee(), relation.getRoom());

        if (image != null) {
            String renameImageName = getRenameImage(image);
            uploadToFileStorage(image, renameImageName);
            String imageUrl = storageService.getUrl(renameImageName);
            message.setImgUrl(imageUrl);
        }

        return messageRepository.saveMessage(message).getId();
    }

    @Override
    @Transactional
    public long sendMessage(Member currentMember, Long roomId,
                            SendMessageRequest request, MultipartFile image) {

        Relation relation = relationRepository.findByRoomIdAndManittoId(roomId, currentMember.getId())
                .orElseThrow(RoomNotParticipatingException::new);

        if (!Objects.equals(relation.getManittee().getId(), UUID.fromString(request.getManitteeId()))) {
            throw new NotManitteeException();
        }

        // TODO: 메시지 생성 메서드
        Message message = Message.builder().content(request.getMessageContent()).build();
        message.sendMessage(relation.getManitto(), relation.getManittee(), relation.getRoom());

        if (image != null) {
            String renameImageName = getRenameImage(image);
            uploadToFileStorage(image, renameImageName);
            String imageUrl = storageService.getUrl(renameImageName);
            message.setImgUrl(imageUrl);
        }

        return messageRepository.saveMessage(message).getId();
    }

    @Override
    public SentMessagesResponse getSentMessages(Member currentMember, Long roomId) {
        throwExceptionIfNotParticipating(currentMember.getId(), roomId);
        Relation relation = throwExceptionIfManitteeNotFound(currentMember.getId(), roomId);
        List<Message> messages = messageRepository.getSentMessages(currentMember.getId(), roomId);
        return SentMessagesResponse.of(messages, relation.getManittee());
    }

    @Override
    public void setReadMessagesStatus(Member currentMember, Long roomId) {
        throwExceptionIfNotParticipating(currentMember.getId(), roomId);
        List<Message> messages = messageRepository
                .findMessagesByReceiverIdAndRoomIdAndStatus(currentMember.getId(), roomId, false);
        for (Message message : messages) {
            message.readMessage();
        }
    }

    @Override
    public MemoriesResponse getMemories(Member currentMember, Long roomId) {
        throwExceptionIfNotParticipating(currentMember.getId(), roomId);
        Relation myManittoRelation = throwExceptionIfManittoNotFound(currentMember.getId(), roomId);
        Relation myManitteeRelation = throwExceptionIfManitteeNotFound(currentMember.getId(), roomId);
        List<Message> receivedMessageImage = messageRepository
                .getTwoRandomImageReceivedMessages(currentMember.getId(), roomId);
        List<Message> receivedMessageContent = messageRepository
                .getTwoRandomContentReceivedMessages(currentMember.getId(), roomId);
        List<Message> sentMessageImage = messageRepository
                .getTwoRandomImageSentMessages(currentMember.getId(), roomId);
        List<Message> sentMessageContent = messageRepository
                .getTwoRandomContentSentMessages(currentMember.getId(), roomId);

        List<Message> receivedMessage = new ArrayList<>();
        receivedMessage.addAll(receivedMessageContent);
        receivedMessage.addAll(receivedMessageImage);
        List<Message> sentMessage = new ArrayList<>();
        sentMessage.addAll(sentMessageContent);
        sentMessage.addAll(sentMessageImage);

        return MemoriesResponse.of(myManittoRelation.getManitto(), myManitteeRelation.getManittee(),
                receivedMessage, sentMessage);
    }

    public ReceivedMessagesResponse getReceivedMessages(Member currentMember, Long roomId) {
        throwExceptionIfNotParticipating(currentMember.getId(), roomId);
        Relation relation = throwExceptionIfManittoNotFound(currentMember.getId(), roomId);
        List<Message> messages = messageRepository.getReceivedMessages(currentMember.getId(), roomId);
        return ReceivedMessagesResponse.of(messages);
    }

    private String getImageExtension(String originalImageName) {
        try {
            return originalImageName.substring(originalImageName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            throw new ImageExtensionNotFoundException();
        }
    }

    // 이미지 구별 위해 rename
    private String getRenameImage(MultipartFile image) {
        return UUID.randomUUID()
                .toString()
                .concat(image.getOriginalFilename())
                .concat(getImageExtension(
                        Objects.requireNonNull(image.getOriginalFilename())
                ));
    }

    private void uploadToFileStorage(MultipartFile image, String renamedImageName) {
        ObjectMetadata objectMetaData = new ObjectMetadata();
        objectMetaData.setContentType(image.getContentType());
        objectMetaData.setContentLength(image.getSize());
        try (InputStream inputStream = image.getInputStream()) {
            storageService.upload(renamedImageName, inputStream, objectMetaData);
        } catch (IOException e) {
            throw new FileUploadException();
        }
    }

    private MemberRoom throwExceptionIfNotParticipating(UUID memberId, Long roomId) {
        return roomRepository.findMemberRoomById(memberId, roomId)
                .orElseThrow(RoomNotParticipatingException::new);
    }

    private Relation throwExceptionIfManitteeNotFound(UUID memberId, Long roomId) {
        return relationRepository.findByRoomIdAndManittoId(roomId, memberId)
                .orElseThrow(RelationNotFoundException::new);
    }

    private Relation throwExceptionIfManittoNotFound(UUID memberId, Long roomId) {
        return relationRepository.findByRoomIdAndManitteeId(roomId, memberId)
                .orElseThrow(RelationNotFoundException::new);
    }
}
