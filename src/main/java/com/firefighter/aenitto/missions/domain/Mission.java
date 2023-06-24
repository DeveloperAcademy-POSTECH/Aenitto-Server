package com.firefighter.aenitto.missions.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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

  public void update(String content) {
    if (content != null) {
      this.content = content;
    }
  }
}
