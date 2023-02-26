package com.firefighter.aenitto.common.postConstruct;

import java.time.LocalDate;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.firefighter.aenitto.common.exception.CustomException;
import com.firefighter.aenitto.missions.service.MissionService;
import com.firefighter.aenitto.rooms.service.RoomService;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostConstructDailyService {
	@Qualifier("missionServiceImpl")
	private final MissionService missionService;

	@Qualifier("roomServiceImpl")
	private final RoomService roomService;

	@PostConstruct
	void setDailyIndividualMissionIfNotSet() {
		try {
			missionService.setDailyIndividualMission(LocalDate.now());
		} catch (CustomException e) {
		}
	}

	@PostConstruct
	void setDailyCommonMissionIfNotSet() {
		try {
			missionService.setDailyCommonMission(LocalDate.now());
		} catch (CustomException e) {
		}
	}

	@PostConstruct
	void endAenittoIfNotEnded() {
		roomService.endAenitto();
	}
}
