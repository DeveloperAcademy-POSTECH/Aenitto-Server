package com.firefighter.aenitto.missions.domain;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Mission {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "mission_id")
  private Long id;

  private String content;

  @Enumerated(value = EnumType.STRING)
  private MissionType type;

  @Builder
  public Mission(String content, MissionType type) {
    this.content = content;
    this.type = type;
  }
}
