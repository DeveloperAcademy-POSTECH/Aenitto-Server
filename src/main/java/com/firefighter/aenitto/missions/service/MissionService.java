package com.firefighter.aenitto.missions.service;

import com.firefighter.aenitto.missions.domain.MissionType;

import java.time.LocalDate;

public interface MissionService {
    Long setDailyCommonMission(LocalDate date);

    void setDailyIndividualMission(LocalDate date);
}
