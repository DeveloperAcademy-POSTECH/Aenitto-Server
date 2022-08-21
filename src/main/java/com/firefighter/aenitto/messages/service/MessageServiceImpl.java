package com.firefighter.aenitto.messages.service;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.firefighter.aenitto.common.exception.message.FileUploadException;
import com.firefighter.aenitto.common.exception.message.ImageExtensionNotFoundException;
import com.firefighter.aenitto.common.exception.message.NotManitteeException;
import com.firefighter.aenitto.common.exception.room.RoomNotParticipatingException;
import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.members.repository.MemberRepository;
import com.firefighter.aenitto.messages.domain.Message;
import com.firefighter.aenitto.messages.dto.request.SendMessageRequest;
import com.firefighter.aenitto.messages.repository.MessageRepository;
import com.firefighter.aenitto.rooms.domain.Relation;
import com.firefighter.aenitto.rooms.repository.RelationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageServiceImpl implements MessageService{

    @Qualifier("memberRepositoryImpl")
    private final MemberRepository memberRepository;

    @Qualifier("relationRepositoryImpl")
    private final RelationRepository relationRepository;

    @Qualifier("messageRepositoryImpl")
    private final MessageRepository messageRepository;

    @Qualifier("StorageS3ServiceImpl")
    private final StorageService storageService;

    @Override
    @Transactional
    public long sendMessage(Member currentMember, Long roomId,
                            SendMessageRequest request, MultipartFile image){

        Relation relation = relationRepository.findByRoomIdAndMemberId(roomId, currentMember.getId())
                .orElseThrow(RoomNotParticipatingException::new);

        if(!Objects.equals(relation.getManittee().getId(), UUID.fromString(request.getManitteeId()))){
            throw new NotManitteeException();
        }

        Message message = Message.builder().content(request.getMessageContent()).build();
        message.sendMessage(relation.getManitto(), relation.getManittee(), relation.getRoom());

        if(image != null){
            String renameImageName  = getRenameImage(image);
            uploadToFileStorage(image, renameImageName);
            String imageUrl = storageService.getUrl(renameImageName);
            message.setImgUrl(imageUrl);
        }

        return messageRepository.saveMessage(message).getId();
    }

    private String getImageExtension(String originalImageName){
        try {
            return originalImageName.substring(originalImageName.lastIndexOf("."));
        }catch (StringIndexOutOfBoundsException e){
            throw new ImageExtensionNotFoundException();
        }
    }

    // 이미지 구별 위해 rename
    private String getRenameImage(MultipartFile image){
        return UUID.randomUUID()
                .toString()
                .concat(image.getOriginalFilename())
                .concat(getImageExtension(
                        Objects.requireNonNull(image.getOriginalFilename())
                ));
    }

    private void uploadToFileStorage(MultipartFile image, String renamedImageName){
        ObjectMetadata objectMetaData = new ObjectMetadata();
        objectMetaData.setContentType(image.getContentType());
        objectMetaData.setContentLength(image.getSize());
        try (InputStream inputStream = image.getInputStream()){
            storageService.upload(renamedImageName, inputStream, objectMetaData);
        }catch (IOException e){
            throw new FileUploadException();
        }
    }
}
