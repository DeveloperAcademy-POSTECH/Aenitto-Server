package com.firefighter.aenitto.missions.controller;

import com.firefighter.aenitto.missions.dto.response.UpdateRequest;
import com.firefighter.aenitto.missions.dto.response.UpdateResponse;
import com.firefighter.aenitto.missions.service.MissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class IndividualMissionController {

  private final MissionService missionService;

  @PatchMapping("/{memberRoomId}/individual-mission")
  public ResponseEntity<UpdateResponse> update(@PathVariable Long memberRoomId,
      @RequestBody UpdateRequest dto) {
    UpdateResponse response = missionService.updateIndividualMission(memberRoomId, dto);
    return ResponseEntity.ok(response);
  }
}
