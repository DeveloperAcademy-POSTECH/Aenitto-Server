package com.firefighter.aenitto.missions.domain;

import com.firefighter.aenitto.rooms.domain.Room;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IndividualMission {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @Column(name = "individual_mission_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "mission_id")
  private Mission mission;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "room_id")
  private Room room;

  private LocalDate date;

  @ColumnDefault(value = "false")
  private boolean fulfilled;

  @Column
  private LocalDateTime fulfilledAt;

  @Builder
  public IndividualMission(LocalDate date) {
    this.date = date;
  }

  private IndividualMission(LocalDate date, Mission mission) {
    this.date = date;
    this.mission = mission;
  }

  public boolean didSet(LocalDate date) {
    return (this.date.isEqual(date));
  }

  public void setRoom(Room room) {
    this.room = room;
  }

  public static IndividualMission of(Mission mission, LocalDate date) {
    return new IndividualMission(date, mission);
  }

  public void changeMission(Mission mission) {
    if (mission != null) {
      this.mission = mission;
    }
  }
}

