package com.firefighter.aenitto.rooms.repository;

import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.rooms.domain.MemberRoom;
import com.firefighter.aenitto.rooms.domain.Room;
import com.firefighter.aenitto.rooms.domain.RoomState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
@ActiveProfiles("testdb")
class RoomRepositoryImplTest {
    @Autowired EntityManager em;
    @Autowired RoomRepositoryImpl roomRepository;

    Room room;
    Member member;
    MemberRoom memberRoom;

    @BeforeEach
    void setRoom() {
        this.room = Room.builder()
                .title("방제목")
                .capacity(10)
                .invitation("123456")
                .startDate(LocalDate.of(2022, 6, 27))
                .endDate(LocalDate.of(2022, 6, 30))
                .build();

        this.member = Member.builder()
                .nickname("Leo")
                .build();

        this.memberRoom = MemberRoom.builder()
                .admin(false)
                .colorIdx(1)
                .build();
    }

    @DisplayName("Room 정보 수정 테스트")
    @Test
    void mergeRoomTest() {
        // given
        roomRepository.saveRoom(room);
        em.flush();
        em.clear();

        // room은 detached 상태
        MemberRoom memberRoom = MemberRoom.builder().build();
        Member member = Member.builder().build();
        memberRoom.setMemberRoom(member, room);

        assertThatExceptionOfType(InvalidDataAccessApiUsageException.class)
                .isThrownBy(() -> {
                    roomRepository.saveRoom(room);
                });

        Room merge = roomRepository.mergeRoom(room);

        Room roomById = roomRepository.findRoomById(merge.getId());

        // then
        assertThat(roomById).isNotNull();
        assertThat(roomById.getId()).isEqualTo(room.getId());
        assertThat(roomById.getMemberRooms().size()).isEqualTo(1);

    }


    @DisplayName("Room Entity 테스트")
    @Test
    void roomEntityTest() {
        // given
        em.persist(room);
        em.flush();

        // when
        Room findRoom = em.find(Room.class, room.getId());

        // then
        assertThat(findRoom.getCapacity()).isEqualTo(10);
        assertThat(findRoom.getState()).isEqualTo(RoomState.PRE);
        assertThat(findRoom.isDeleted()).isEqualTo(false);
        assertThat(findRoom.getStartDate()).isEqualTo(LocalDate.of(2022, 6, 27));
    }


    @DisplayName("RoomRepository Room 저장 테스트")
    @Test
    void persistRoom() {
        // given
        roomRepository.saveRoom(room);
        em.flush();

        // when
        Room roomById = roomRepository.findRoomById(room.getId());

        // then
        assertThat(room).isEqualTo(roomById);
    }

    @DisplayName("초대코드로 방 검색 -> 성공")
    @Test
    void findByInvitationTest() {
        // given
        roomRepository.saveRoom(room);
        em.flush();

        // when
        Room byInvitation = roomRepository.findByInvitation("123456");

        // then
        assertThat(byInvitation).isEqualTo(room);
        assertThat(byInvitation.getTitle()).isEqualTo("방제목");
    }

    @DisplayName("초대코드로 방 검색 -> 실패 (초대코드 없음)")
    @Test
    void findByInvitationTestFailure() {
        // given
        roomRepository.saveRoom(room);
        em.flush();

        // exception throw
        assertThatThrownBy(() -> {
            Room byInvitation = roomRepository.findByInvitation("123466");
        })
                .isInstanceOf(EmptyResultDataAccessException.class);

    }

    @DisplayName("MemberRoom 연관관계 메서드 setMemberRoom")
    @Test
    void setMemberRoomTest() {
        // given
        Member member = Member.builder().build();
        em.persist(member);
        roomRepository.saveRoom(room);

        MemberRoom memberRoom = MemberRoom.builder().admin(false).build();
        memberRoom.setMemberRoom(member, room);
        em.flush();

        // when
        MemberRoom memberRoom1 = em.find(MemberRoom.class, memberRoom.getId());

        // then
        assertThat(memberRoom1).isEqualTo(memberRoom);
        assertThat(memberRoom1.getMember()).isEqualTo(member);
        assertThat(memberRoom1.getRoom()).isEqualTo(room);
    }

    @DisplayName("roomId, memberId로 MemberRoom 검색 - 실패 (memberRoom 없음)")
    @Test
    void findMemberRoomById_fail_not_exist() {
        // given, when, then
        assertThatExceptionOfType(EmptyResultDataAccessException.class)
                .isThrownBy(() -> {
                    roomRepository.findMemberRoomById(UUID.randomUUID(), 1L);
                });
    }

    @DisplayName("roomId, memberId로 MemberRoom 검색 - 성공")
    @Test
    void findMemberRoomById() {
        // given
        memberRoom.setMemberRoom(member, room);
        em.persist(member);
        em.persist(room);

        em.flush();
        em.clear();

        // when
        MemberRoom memberRoomById = roomRepository.findMemberRoomById(member.getId(), room.getId());

        // then
        assertThat(memberRoomById).isNotNull();
        assertThat(memberRoomById.getColorIdx()).isEqualTo(1);
        assertThat(memberRoomById.isAdmin()).isEqualTo(false);
    }

    @DisplayName("방 참가 테스트 (Member, Room - detached, MemberRoom - transient")
    @Test
    void participateRoom() {
        // given
        em.persist(member);
        em.persist(room);

        em.flush();
        em.clear();

        MemberRoom memberRoom = MemberRoom.builder().admin(false).colorIdx(1).build();

        // when
        Room findRoom = em.find(Room.class, room.getId());
        memberRoom.setMemberRoom(member, findRoom);
        em.merge(member);

        em.flush();
        em.clear();

        // then
        Member member1 = em.find(Member.class, member.getId());
        MemberRoom memberRoom1 = em.find(MemberRoom.class, memberRoom.getId());
        Room room1 = em.find(Room.class, room.getId());

        assertNotNull(member1);
        assertNotNull(room1);
        assertNotNull(memberRoom1);
        assertThat(member1.getMemberRooms().size()).isEqualTo(1);
        assertThat(room1.getMemberRooms().size()).isEqualTo(1);
    }

    @DisplayName("참여 중인 방 조회 - 성공 (cursor null)")
    @Test
    void findParticipatingRooms_success_without_cursor() {
        // given
        Room room1 = Room.builder().title("방 제목1").build();
        Room room2 = Room.builder().title("방 제목2").build();
        Room room3 = Room.builder().title("방 제목3").build();
        Room room4 = Room.builder().title("방 제목4").build();
        Room room5 = Room.builder().title("방 제목5").build();

        MemberRoom memberRoom1 = MemberRoom.builder().build();
        MemberRoom memberRoom2 = MemberRoom.builder().build();
        MemberRoom memberRoom3 = MemberRoom.builder().build();
        MemberRoom memberRoom4 = MemberRoom.builder().build();
        MemberRoom memberRoom5 = MemberRoom.builder().build();

        memberRoom1.setMemberRoom(member, room1);
        memberRoom2.setMemberRoom(member, room2);
        memberRoom3.setMemberRoom(member, room3);
        memberRoom4.setMemberRoom(member, room4);
        memberRoom5.setMemberRoom(member, room5);

        em.persist(member);
        em.persist(room1);
        em.persist(room2);
        em.persist(room3);
        em.persist(room4);
        em.persist(room5);

        em.flush();
        em.clear();

        // when
        List<Room> res = roomRepository.findParticipatingRoomsByMemberIdWithCursor(member.getId(), null, 3);

        // then
        assertThat(res.size()).isEqualTo(3);
        assertThat(res.get(0).getTitle()).isEqualTo("방 제목5");
        assertThat(res.get(1).getTitle()).isEqualTo("방 제목4");
        assertThat(res.get(2).getTitle()).isEqualTo("방 제목3");
    }

    @DisplayName("참여 중인 방 조회 - 성공 (cursor 존재")
    @Test
    void findParticipatinRoom_success_without_cursor() {
        // given
        Room room1 = Room.builder().title("방 제목1").build();
        Room room2 = Room.builder().title("방 제목2").build();
        Room room3 = Room.builder().title("방 제목3").build();
        Room room4 = Room.builder().title("방 제목4").build();
        Room room5 = Room.builder().title("방 제목5").build();

        MemberRoom memberRoom1 = MemberRoom.builder().build();
        MemberRoom memberRoom2 = MemberRoom.builder().build();
        MemberRoom memberRoom3 = MemberRoom.builder().build();
        MemberRoom memberRoom4 = MemberRoom.builder().build();
        MemberRoom memberRoom5 = MemberRoom.builder().build();

        memberRoom1.setMemberRoom(member, room1);
        memberRoom2.setMemberRoom(member, room2);
        memberRoom3.setMemberRoom(member, room3);
        memberRoom4.setMemberRoom(member, room4);
        memberRoom5.setMemberRoom(member, room5);

        em.persist(member);
        em.persist(room1);
        em.persist(room2);
        em.persist(room3);
        em.persist(room4);
        em.persist(room5);

        em.flush();
        em.clear();

        // when
        List<Room> res = roomRepository.findParticipatingRoomsByMemberIdWithCursor(member.getId(), room3.getId(), 3);

        // then
        assertThat(res.size()).isEqualTo(2);
        assertThat(res.get(0).getTitle()).isEqualTo("방 제목2");
        assertThat(res.get(1).getTitle()).isEqualTo("방 제목1");
    }
}