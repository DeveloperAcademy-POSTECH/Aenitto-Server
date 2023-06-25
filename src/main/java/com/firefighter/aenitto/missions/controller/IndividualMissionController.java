package com.firefighter.aenitto.missions.controller;

import com.firefighter.aenitto.common.annotation.CurrentMember;
import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.missions.dto.response.UpdateRequest;
import com.firefighter.aenitto.missions.dto.response.UpdateResponse;
import com.firefighter.aenitto.missions.service.MissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

  @PatchMapping("/{roomId}/individual-mission")
  public ResponseEntity<UpdateResponse> update(@CurrentMember final Member member, @PathVariable Long roomId,
      @RequestBody UpdateRequest dto) {
    UpdateResponse response = missionService.updateIndividualMission(member, roomId, dto);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{roomId}/individual-mission/restore")
  public ResponseEntity<UpdateResponse> restore(@CurrentMember final Member member, @PathVariable Long roomId) {
    UpdateResponse response = missionService.restoreIndividualMission(member, roomId);
    return ResponseEntity.ok(response);
  }
}
