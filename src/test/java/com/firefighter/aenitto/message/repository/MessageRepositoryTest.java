package com.firefighter.aenitto.message.repository;

import com.firefighter.aenitto.members.MemberFixture;
import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.message.MessageFixture;
import com.firefighter.aenitto.messages.domain.Message;
import com.firefighter.aenitto.messages.repository.MessageRepositoryImpl;
import com.firefighter.aenitto.rooms.RoomFixture;
import com.firefighter.aenitto.rooms.domain.Room;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class MessageRepositoryTest {
    @Autowired EntityManager em;
    @Autowired MessageRepositoryImpl messageRepository;

    Message message1;
    Message message2;
    Message message3;
    Message message4;
    Message message5;
    Message message6;
    Message message7;

    List<Message> messages = new ArrayList<>();
    Room room1;
    Member member1;
    Member member2;

    @BeforeEach
    void setInit() {
        this.message1 = MessageFixture.transientMessageFixture(1);
        this.message2 = MessageFixture.transientMessageFixture(2);
        this.message3 = MessageFixture.transientMessageFixture(3);
        this.message4 = MessageFixture.transientMessageFixture(4);
        this.message5 = MessageFixture.transientMessageFixture(5);
        this.message6 = MessageFixture.transientMessageFixture(6);
        this.message7 = MessageFixture.transientMessageFixture(7);

        messages.add(message1);
        messages.add(message2);
        messages.add(message3);
        messages.add(message4);
        messages.add(message5);
        messages.add(message6);
        messages.add(message7);

        this.room1 = RoomFixture.transientRoomFixture(1, 10, 10);
        this.member1 = MemberFixture.transientMemberFixture(1);
        this.member2 = MemberFixture.transientMemberFixture(2);
    }

    @DisplayName("읽지 않은 메시지 개수 - 성공")
    @Test
    void findUnreadMessageCount_success() {
        // given
        for (Message message : messages) {
            message.sendMessage(member1, member2, room1);
        }

        message1.readMessage();
        message2.readMessage();

        em.persist(member1);
        em.persist(member2);
        em.persist(room1);
        for (Message message : messages) {
            em.persist(message);
        }
        em.flush();

        // when
        int unreadMessageCount1 = messageRepository.findUnreadMessageCount(member1.getId(), room1.getId());
        int unreadMessageCount2 = messageRepository.findUnreadMessageCount(member2.getId(), room1.getId());

        // then
        assertThat(unreadMessageCount1).isEqualTo(0);
        assertThat(unreadMessageCount2).isEqualTo(5);
    }

    @DisplayName("메시지 보내기 - 성공")
    @Test
    void sendMessage_success(){
        //given
        messageRepository.saveMessage(message1);
        em.flush();

        //when
        Message result = em.find(Message.class, message1.getId());

        //then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getContent()).isNotNull();
        assertThat(result.getImgUrl()).isNotNull();
    }

    @DisplayName("보낸 메시지 가져오기 - 실패")
    @Test
    void getSentMessages_failure() {
        assertThat(messageRepository.getSentMessages(UUID.randomUUID(), 1L).isEmpty()).isTrue();
    }

    @DisplayName("보낸 메시지 가져오기 - 성공")
    @Test
    void getSentMessages_success() {
        // given
        for (Message message : messages) {
            message.sendMessage(member1, member2, room1);
        }

        em.persist(member1);
        em.persist(member2);
        em.persist(room1);

        for (Message message : messages) {
            em.persist(message);
        }
        em.flush();

        // when
        List<Message> result = messageRepository.getSentMessages(member1.getId(), room1.getId());

        // then
        assertThat(result.size()).isEqualTo(7);
    }

    @DisplayName("받은 메시지 가져오기 - 실패")
    @Test
    void getRecievedMessages_failure() {
        assertThat(messageRepository.getSentMessages(UUID.randomUUID(), 1L).isEmpty()).isTrue();
    }

    @DisplayName("받은 메시지 가져오기 - 성공")
    @Test
    void getRecievedMessages_success() {
        // given
        for (Message message : messages) {
            message.sendMessage(member1, member2, room1);
        }

        em.persist(member1);
        em.persist(member2);
        em.persist(room1);

        for (Message message : messages) {
            em.persist(message);
        }
        em.flush();

        // when
        List<Message> result = messageRepository.getReceivedMessages(member2.getId(), room1.getId());

        // then
        assertThat(result.size()).isEqualTo(7);
    }
}
