package com.firefighter.aenitto.missions.controller;

import com.firefighter.aenitto.common.annotation.CurrentMember;
import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.missions.dto.response.DailyCommonMissionResponse;
import com.firefighter.aenitto.missions.service.MissionService;
import com.firefighter.aenitto.rooms.dto.response.GetRoomStateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
