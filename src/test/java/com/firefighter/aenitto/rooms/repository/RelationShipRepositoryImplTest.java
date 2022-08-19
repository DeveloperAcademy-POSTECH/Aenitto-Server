package com.firefighter.aenitto.rooms.repository;

import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.rooms.domain.Relation;
import com.firefighter.aenitto.rooms.domain.Room;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Optional;
import java.util.UUID;

import static com.firefighter.aenitto.members.MemberFixture.memberFixture;
import static com.firefighter.aenitto.members.MemberFixture.memberFixture2;
import static com.firefighter.aenitto.rooms.RoomFixture.roomFixture1;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class RelationShipRepositoryImplTest {
    @Autowired
    EntityManager em;

    @Autowired
    RelationRepositoryImpl target;

    Member manitto;
    Member manittee;
    Room room;

    @BeforeEach
    void setUp() {
        manittee = memberFixture();
        manitto = memberFixture2();
        room = roomFixture1();
    }

    @DisplayName("roomId 와 memberId로 relation찾기 - 성공")
    @Test()
    @Sql("classpath:relation.sql")
    void findByRoomIdAndMemberId() {
        // when
        final Optional<Relation> result = target.findByRoomIdAndMemberId(1L,
                UUID.fromString("a383cdb3-a871-4410-b146-fb1f7b447b9e"));

        // then
        assertThat(result).isNotNull();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getRoom().getId()).isEqualTo(1L);
        assertThat(result.get().getManittee().getNickname()).isEqualTo("manittee");
    }
}
