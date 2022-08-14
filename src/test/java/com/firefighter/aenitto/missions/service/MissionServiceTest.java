package com.firefighter.aenitto.missions.service;

import com.firefighter.aenitto.common.exception.mission.MissionAlreadySetException;
import com.firefighter.aenitto.common.exception.mission.MissionEmptyException;
import com.firefighter.aenitto.missions.domain.CommonMission;
import com.firefighter.aenitto.missions.domain.Mission;
import com.firefighter.aenitto.missions.domain.MissionType;
import com.firefighter.aenitto.missions.repository.MissionRepository;
import com.firefighter.aenitto.missions.repository.MissionRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static com.firefighter.aenitto.missions.CommonMissionFixture.*;
import static com.firefighter.aenitto.missions.MissionFixture.*;

@ExtendWith(MockitoExtension.class)
public class MissionServiceTest {
    @InjectMocks
    private MissionServiceImpl missionService;

    @Mock
    private MissionRepositoryImpl missionRepository;

    Mission mission1_common;
    CommonMission commonMission1;

    @BeforeEach
    void init() {
        mission1_common = missionFixture1_Common();
        commonMission1 = commonMissionFixture1();
    }

    @DisplayName("해당 일자의 CommonMission 세팅 - 실패 (해당 일자에 CommonMission 이 이미 존재)")
    @Test
    void setDailyCommonMission_fail_already_exist() {
        // when
        when(missionRepository.findCommonMissionByDate(any(LocalDate.class)))
                .thenReturn(Optional.of(commonMission1));

        // then
        assertThatExceptionOfType(MissionAlreadySetException.class)
                .isThrownBy(() -> {
                    missionService.setDailyCommonMission(LocalDate.now());
                });
    }

    @DisplayName("해당 일자의 CommonMission 세팅 - 실패 (Mission 테이블이 비어있음)")
    @Test
    void setDailyCommonMission_fail_mission_empty() {
        // when
        when(missionRepository.findCommonMissionByDate(any(LocalDate.class)))
                .thenReturn(Optional.empty());
        when(missionRepository.findRandomMission(any(MissionType.class)))
                .thenReturn(Optional.empty());

        // then
        assertThatExceptionOfType(MissionEmptyException.class)
                .isThrownBy(() -> {
                    missionService.setDailyCommonMission(LocalDate.now());
                });
    }

    @DisplayName("해당 일자의 CommonMission 세팅 - 성공")
    @Test
    void setDailyCommonMission_success() {
        // given

        // when
        when(missionRepository.findCommonMissionByDate(any(LocalDate.class)))
                .thenReturn(Optional.empty());
        when(missionRepository.findRandomMission(any(MissionType.class)))
                .thenReturn(Optional.of(mission1_common));
        when(missionRepository.saveCommonMission(any(CommonMission.class)))
                .thenReturn(commonMission1);

        Long commonMissionId = missionService.setDailyCommonMission(LocalDate.now());

        // then
        assertThat(commonMissionId).isEqualTo(commonMission1.getId());
    }
}
