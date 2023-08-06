package com.firefighter.aenitto.missions.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommonMission {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "common_mission_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "mission_id")
  private Mission mission;

  private LocalDate date;

  @Builder
  public CommonMission(LocalDate date) {
    this.date = date;
  }

  private CommonMission(LocalDate date, Mission mission) {
    this.date = date;
    this.mission = mission;
  }

  public static CommonMission createCommonMission(LocalDate date, Mission mission) {
    return new CommonMission(date, mission);
  }
}
