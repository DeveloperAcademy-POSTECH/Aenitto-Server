package com.firefighter.aenitto.missions.domain;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

public class DefaultMission {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "mission_id")
  private Mission mission;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "individual_mission_id")
  @Column(updatable = false)
  private IndividualMission individualMission;

  public DefaultMission(Mission mission, IndividualMission individualMission) {
    this.mission = mission;
    this.individualMission = individualMission;
  }

  public void changeDefaultMission(Mission mission) {
    if (mission != null) {
      this.mission = mission;
    }
  }

  public Long getId() {
    return id;
  }

  public Mission getMission() {
    return mission;
  }

  public IndividualMission getIndividualMission() {
    return individualMission;
  }
}
