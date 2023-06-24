package com.firefighter.aenitto.missions.dto.response;

import com.firefighter.aenitto.missions.domain.Mission;

public class UpdateResponse {

  private final String mission;

  public UpdateResponse(String mission) {
    this.mission = mission;
  }

  public static UpdateResponse fromEntity(Mission mission) {
    return new UpdateResponse(
        mission.getContent()
    );
  }

  public String getMission() {
    return mission;
  }
}
