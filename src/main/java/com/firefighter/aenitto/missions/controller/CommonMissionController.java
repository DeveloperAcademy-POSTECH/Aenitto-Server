package com.firefighter.aenitto.missions.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.firefighter.aenitto.missions.dto.response.DailyCommonMissionResponse;
import com.firefighter.aenitto.missions.service.MissionService;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CommonMissionController {
	@Qualifier("missionServiceImpl")
	private final MissionService missionService;

	@GetMapping("/missions/common")
	public ResponseEntity<DailyCommonMissionResponse> getCommonMission() {
		return ResponseEntity.ok(missionService.getDailyCommonMission());
	}
}
