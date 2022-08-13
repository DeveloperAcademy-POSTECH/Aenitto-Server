package com.firefighter.aenitto.missions.repository;

import com.firefighter.aenitto.missions.MissionFixture;
import com.firefighter.aenitto.missions.domain.Mission;
import com.firefighter.aenitto.missions.domain.MissionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.firefighter.aenitto.missions.MissionFixture.*;
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
}
