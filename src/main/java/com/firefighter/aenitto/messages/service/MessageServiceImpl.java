package com.firefighter.aenitto.messages.service;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.firefighter.aenitto.common.exception.message.FileUploadException;
import com.firefighter.aenitto.common.exception.message.ImageExtensionNotFoundException;
import com.firefighter.aenitto.common.exception.message.NotManitteeException;
import com.firefighter.aenitto.common.exception.mission.MissionNotExistException;
import com.firefighter.aenitto.common.exception.room.RelationNotFoundException;
import com.firefighter.aenitto.common.exception.room.RoomNotParticipatingException;
import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.messages.domain.Message;
import com.firefighter.aenitto.messages.dto.api.SendMessageApiDto;
import com.firefighter.aenitto.messages.dto.request.SendMessageRequest;
import com.firefighter.aenitto.messages.dto.response.MemoriesResponse;
import com.firefighter.aenitto.messages.dto.response.ReceivedMessagesResponse;
import com.firefighter.aenitto.messages.dto.response.SentMessagesResponse;
import com.firefighter.aenitto.messages.dto.response.version2.MessageResponseV2;
import com.firefighter.aenitto.messages.dto.response.version2.ReceivedMessagesResponseV2;
import com.firefighter.aenitto.messages.dto.response.version2.SentMessagesResponseV2;
import com.firefighter.aenitto.messages.repository.MessageRepository;
import com.firefighter.aenitto.missions.domain.Mission;
import com.firefighter.aenitto.missions.repository.MissionRepository;
import com.firefighter.aenitto.notification.service.NotificationService;
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
  @Qualifier("relationRepositoryImpl")
  private final RelationRepository relationRepository;

  @Qualifier("messageRepositoryImpl")
  private final MessageRepository messageRepository;

  @Qualifier("roomRepositoryImpl")
  private final RoomRepository roomRepository;

  @Qualifier("StorageS3ServiceImpl")
  private final StorageService storageService;

  @Qualifier("fcmNotificationService")
  private final NotificationService notificationService;

  private final MissionRepository missionRepository;

  @Override
  @Transactional
  public long sendMessageSeparate(Member currentMember, SendMessageApiDto dto) {
    Relation relation = relationRepository.findByRoomIdAndManittoId(dto.getRoomId(), currentMember.getId())
        .orElseThrow(RoomNotParticipatingException::new);

    throwIfIdNotIdentical(relation.getManittee().getId(), dto.getManitteeId());

    Message message = initializeMessage(relation, dto);

    if (relation.getManittee().getFcmToken() != null) {
      notificationService.sendMessage(relation.getManittee().getFcmToken(),
          "마니또로부터 메시지가 도착하였습니다.", message.getContent(), relation.getRoom().getId().toString());
    }
    return messageRepository.saveMessage(message).getId();
  }

  @Deprecated
  @Override
  @Transactional
  public long sendMessage(Member currentMember, Long roomId, SendMessageRequest request, MultipartFile image) {

    Relation relation = relationRepository.findByRoomIdAndManittoId(roomId, currentMember.getId())
        .orElseThrow(RoomNotParticipatingException::new);

    if (!Objects.equals(relation.getManittee().getId(), UUID.fromString(request.getManitteeId()))) {
      throw new NotManitteeException();
    }

    // TODO: 메시지 생성 메서드
    Message message = Message.builder().content(request.getMessageContent()).build();
    message.sendMessage(relation.getManitto(), relation.getManittee(), relation.getRoom());

    if (image != null) {
      String renameImageName = getIdentifiableImageName(image);
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
    for (Message message : messages) {
      message.readMessage();
    }
    return SentMessagesResponse.of(messages, relation.getManittee());
  }

  @Override
  public void setReadMessagesStatus(Member currentMember, Long roomId) {
    throwExceptionIfNotParticipating(currentMember.getId(), roomId);
    List<Message> messages = messageRepository.findMessagesByReceiverIdAndRoomIdAndStatus(currentMember.getId(),
        roomId, false);
    for (Message message : messages) {
      message.readMessage();
    }
  }

  @Override
  public MemoriesResponse getMemories(Member currentMember, Long roomId) {
    throwExceptionIfNotParticipating(currentMember.getId(), roomId);
    Relation myManittoRelation = throwExceptionIfManittoNotFound(currentMember.getId(), roomId);
    Relation myManitteeRelation = throwExceptionIfManitteeNotFound(currentMember.getId(), roomId);
    MemberRoom myManitto = throwExceptionIfNotParticipating(myManittoRelation.getManitto().getId(), roomId);
    MemberRoom myManittee = throwExceptionIfNotParticipating(myManitteeRelation.getManittee().getId(), roomId);
    List<Message> receivedMessageImage = messageRepository.getTwoRandomImageReceivedMessages(currentMember.getId(),
        roomId);
    List<Message> receivedMessageContent = messageRepository.getTwoRandomContentReceivedMessages(
        currentMember.getId(), roomId);
    List<Message> sentMessageImage = messageRepository.getTwoRandomImageSentMessages(currentMember.getId(), roomId);
    List<Message> sentMessageContent = messageRepository.getTwoRandomContentSentMessages(currentMember.getId(),
        roomId);

    List<Message> receivedMessage = new ArrayList<>();
    receivedMessage.addAll(receivedMessageContent);
    receivedMessage.addAll(receivedMessageImage);
    List<Message> sentMessage = new ArrayList<>();
    sentMessage.addAll(sentMessageContent);
    sentMessage.addAll(sentMessageImage);

    return MemoriesResponse.of(myManitto, myManittee, receivedMessage, sentMessage);
  }

  @Override
  public SentMessagesResponseV2 getSentMessagesV2(Member currentMember, Long roomId) {
    throwExceptionIfNotParticipating(currentMember.getId(), roomId);
    Relation relation = throwExceptionIfManitteeNotFound(currentMember.getId(), roomId);
    List<Message> messages = messageRepository.getSentMessages(currentMember.getId(), roomId);

    for (Message message : messages) {
      message.readMessage();
    }
    SentMessagesResponseV2 sentMessagesResponse = SentMessagesResponseV2.of(messages, relation.getManittee());
    setMission(sentMessagesResponse.getMessages());

    return sentMessagesResponse;
  }

  public List<MessageResponseV2> setMission(List<MessageResponseV2> messagesResponse) {
    messagesResponse.stream().filter(message -> message.hasMission())
        .forEach(messageWithMission ->
            messageWithMission.getMissionInfo()
                .setContent(throwExceptionMissionNotExist(messageWithMission.getMissionInfo()
                    .getId()).getContent()));

    return messagesResponse;
  }

  public ReceivedMessagesResponse getReceivedMessages(Member currentMember, Long roomId) {
    throwExceptionIfNotParticipating(currentMember.getId(), roomId);
    Relation relation = throwExceptionIfManittoNotFound(currentMember.getId(), roomId);
    List<Message> messages = messageRepository.getReceivedMessages(currentMember.getId(), roomId);
    return ReceivedMessagesResponse.of(messages);
  }

  public ReceivedMessagesResponseV2 getReceivedMessagesV2(Member currentMember, Long roomId) {
    throwExceptionIfNotParticipating(currentMember.getId(), roomId);
    Relation relation = throwExceptionIfManittoNotFound(currentMember.getId(), roomId);
    List<Message> messages = messageRepository.getReceivedMessages(currentMember.getId(), roomId);
    ReceivedMessagesResponseV2 receivedMessagesResponseV2 = ReceivedMessagesResponseV2.of(messages);
    setMission(receivedMessagesResponseV2.getMessages());
    return receivedMessagesResponseV2;
  }

  private Message initializeMessage(Relation relation, SendMessageApiDto dto) {
    Message message = Message.initializeMessageRelationship(dto.getMessageContent(), relation);
    if (dto.isImageNotNull()) {
      String imgUrl = uploadAndGetSavedImgUrl(dto.getImage());
      message.setImgUrl(imgUrl);
    }
    if (dto.isMissionIdNotNull()) {
      message.setMissionId(Long.parseLong(dto.getMissionId()));
    }
    return message;
  }

  private String uploadAndGetSavedImgUrl(MultipartFile image) {
    String imageName = getIdentifiableImageName(image);
    uploadToFileStorage(image, imageName);
    return storageService.getUrl(imageName);
  }

  private String getImageExtension(String originalImageName) {
    try {
      return originalImageName.substring(originalImageName.lastIndexOf("."));
    } catch (StringIndexOutOfBoundsException e) {
      throw new ImageExtensionNotFoundException();
    }
  }

  private String getIdentifiableImageName(MultipartFile image) {
    return UUID.randomUUID()
        .toString()
        .concat(image.getOriginalFilename())
        .concat(getImageExtension(Objects.requireNonNull(image.getOriginalFilename())));
  }

  private void uploadToFileStorage(MultipartFile image, String imageName) {
    ObjectMetadata objectMetaData = new ObjectMetadata();
    objectMetaData.setContentType(image.getContentType());
    objectMetaData.setContentLength(image.getSize());
    try (InputStream inputStream = image.getInputStream()) {
      storageService.upload(imageName, inputStream, objectMetaData);
    } catch (IOException e) {
      throw new FileUploadException();
    }
  }

  private void throwIfIdNotIdentical(UUID idFromRepository, UUID idFromRequest) throws NotManitteeException {
    if (!Objects.equals(idFromRepository, idFromRequest)) {
      throw new NotManitteeException();
    }
  }

  private MemberRoom throwExceptionIfNotParticipating(UUID memberId, Long roomId) {
    return roomRepository.findMemberRoomById(memberId, roomId).orElseThrow(RoomNotParticipatingException::new);
  }

  private Relation throwExceptionIfManitteeNotFound(UUID memberId, Long roomId) {
    return relationRepository.findByRoomIdAndManittoId(roomId, memberId)
        .orElseThrow(RelationNotFoundException::new);
  }

  private Relation throwExceptionIfManittoNotFound(UUID memberId, Long roomId) {
    return relationRepository.findByRoomIdAndManitteeId(roomId, memberId)
        .orElseThrow(RelationNotFoundException::new);
  }

  private Mission throwExceptionMissionNotExist(Long missionId) {
    return missionRepository.findById(missionId)
        .orElseThrow(MissionNotExistException::new);
  }
}
