package com.firefighter.aenitto.missions.dto.response;

public class UpdateRequest {

  private String mission;

  protected UpdateRequest() {

  }

  public UpdateRequest(String mission) {
    this.mission = mission;
  }

  public String getMission() {
    return mission;
  }
}
