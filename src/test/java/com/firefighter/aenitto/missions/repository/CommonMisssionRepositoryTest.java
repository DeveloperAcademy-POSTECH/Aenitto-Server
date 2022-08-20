package com.firefighter.aenitto.missions.repository;

import com.firefighter.aenitto.missions.domain.CommonMission;
import com.firefighter.aenitto.missions.domain.Mission;
import com.firefighter.aenitto.missions.domain.MissionType;
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

import static com.firefighter.aenitto.missions.CommonMissionFixture.transientCommonMissionFixture;
import static com.firefighter.aenitto.missions.MissionFixture.transientMissionFixture;
import static com.firefighter.aenitto.rooms.RoomFixture.transientMemberRoomFixture;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class CommonMisssionRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired CommonMissionRepositoryImpl repository;

    Mission mission1;

    CommonMission commonMission1;

    @BeforeEach
    void init() {
        mission1 = transientMissionFixture(1, MissionType.COMMON);
        commonMission1 = transientCommonMissionFixture();
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
