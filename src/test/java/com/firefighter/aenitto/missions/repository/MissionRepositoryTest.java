package com.firefighter.aenitto.missions.repository;

import com.firefighter.aenitto.missions.MissionFixture;
import com.firefighter.aenitto.missions.domain.CommonMission;
import com.firefighter.aenitto.missions.domain.Mission;
import com.firefighter.aenitto.missions.domain.MissionType;
import com.firefighter.aenitto.rooms.domain.MemberRoom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.firefighter.aenitto.missions.MissionFixture.*;
import static com.firefighter.aenitto.missions.CommonMissionFixture.*;
import static com.firefighter.aenitto.rooms.RoomFixture.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class MissionRepositoryTest {
    @Autowired EntityManager em;
    @Autowired MissionRepositoryImpl repository;

    Mission mission1;
    Mission mission2;
    Mission mission3;
    Mission mission4;
    Mission mission5;
    Mission mission6;
    List<Mission> missions = new ArrayList<>();

    CommonMission commonMission1;

    MemberRoom memberRoom1;

    @BeforeEach
    void init() {
        mission1 = transientMissionFixture(1, MissionType.COMMON);
        mission2 = transientMissionFixture(2, MissionType.COMMON);
        mission3 = transientMissionFixture(3, MissionType.COMMON);
        mission4 = transientMissionFixture(4, MissionType.INDIVIDUAL);
        mission5 = transientMissionFixture(5, MissionType.INDIVIDUAL);
        mission6 = transientMissionFixture(6, MissionType.INDIVIDUAL);

        missions.add(mission1);
        missions.add(mission2);
        missions.add(mission3);
        missions.add(mission4);
        missions.add(mission5);
        missions.add(mission6);

        commonMission1 = transientCommonMissionFixture();

        memberRoom1 = transientMemberRoomFixture(1);
    }

    @DisplayName("랜덤 미션 찾기 - 실패 (미션 없음)")
    @Test
    void findRandomMission_fail_no_mission() {
        // given, when
        Optional<Mission> randomMission = repository.findRandomMission(MissionType.COMMON);

        // then
        assertThat(randomMission.isEmpty()).isTrue();
    }

    @DisplayName("랜덤 미션 찾기 - 성공 (Common 미션)")
    @Test
    void findRandomMission_success_common() {
        // given
        for (Mission mission : missions) {
            em.persist(mission);
        }

        em.flush();
        em.clear();

        // when
        Optional<Mission> randomMission = repository.findRandomMission(MissionType.COMMON);

        // then
        assertThat(randomMission.get()).isNotNull();
        assertThat(randomMission.get().getType()).isEqualTo(MissionType.COMMON);
    }

    @DisplayName("랜덤 미션 찾기 - 성공 (Individual 미션")
    @Test
    void findRandomMission_success_individual() {
        // given
        for (Mission mission : missions) {
            em.persist(mission);
        }

        em.flush();
        em.clear();

        // when
        Optional<Mission> randomMission = repository.findRandomMission(MissionType.INDIVIDUAL);

        // then
        assertThat(randomMission.get()).isNotNull();
        assertThat(randomMission.get().getType()).isEqualTo(MissionType.INDIVIDUAL);
    }

    @DisplayName("오늘 자 공통미션 찾기 - 실패")
    @Test
    void findTodayCommonMission_fail() {
        assertThat(repository.findCommonMissionByDate(LocalDate.now()).isEmpty()).isTrue();
    }

    @DisplayName("오늘 자 공통미션 칮기 - 성공")
    @Test
    void findTodayCommonMission_success() {
        // given
        ReflectionTestUtils.setField(commonMission1, "date", LocalDate.now());
        ReflectionTestUtils.setField(commonMission1, "mission", mission1);

        em.persist(mission1);
        em.persist(commonMission1);

        em.flush();
        em.clear();

        // when
        Optional<CommonMission> todayCommonMission = repository.findCommonMissionByDate(LocalDate.now());

        // then
        assertThat(todayCommonMission.get().getId()).isEqualTo(commonMission1.getId());
        assertThat(todayCommonMission.get().getMission().getType()).isEqualTo(MissionType.COMMON);
        assertThat(todayCommonMission.get().getMission().getContent()).isEqualTo(mission1.getContent());
    }
}
