package com.firefighter.aenitto.common.scheduler;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.firefighter.aenitto.common.exception.CustomException;
import com.firefighter.aenitto.missions.service.MissionService;
import com.firefighter.aenitto.rooms.service.RoomService;

@Slf4j
@Service
@EnableAsync
@RequiredArgsConstructor
public class SchedulerService {
	private final static String LOCAL_TIMEZONE = "Asia/Seoul";

	@Qualifier("missionServiceImpl")
	private final MissionService missionService;

	@Qualifier("roomServiceImpl")
	private final RoomService roomService;

	// TODO: Logger 설정 -> scheduler 로 서버 내부에서 돌릴 경우 exception throw 하지 않고 logging 남기는 방식으로 (22.09.04 - Leo)
	@Async
	@Scheduled(cron = "0 55 23 * * ?", zone = LOCAL_TIMEZONE)
	// 0초 55분 23시
	void setDailyMissions() {
		log.info("SchedulerService::setDailyMissions - START");
		try {
			missionService.setDailyCommonMission(LocalDate.now().plusDays(1));
			missionService.setDailyIndividualMission(LocalDate.now().plusDays(1));
		} catch (CustomException e) {
			log.warn("daily mission 을 설정하는데 문제가 생겼네요. \nmessage: {}", e.getMessage());
		}
		log.info("SchedulerService::setDailyMissions - END");
	}

	@Async
	@Scheduled(cron = "0 0 0 * * ?", zone = LOCAL_TIMEZONE)
	void scheduleEndAenitto() {
		log.info("SchedulerService::endAenitto - START");
		roomService.endAenitto();
		log.info("SchedulerService::endAenitto - END");
	}
}
