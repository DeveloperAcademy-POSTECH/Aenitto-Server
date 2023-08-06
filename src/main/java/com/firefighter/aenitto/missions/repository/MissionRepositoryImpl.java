package com.firefighter.aenitto.missions.repository;

import com.firefighter.aenitto.missions.domain.Mission;
import com.firefighter.aenitto.missions.domain.MissionType;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MissionRepositoryImpl implements MissionRepositoryCustom {

  private final EntityManager em;

  @Override
  public Optional<Mission> findRandomMission(MissionType missionType) {
    return em.createQuery(
            "SELECT m" +
                " FROM Mission m" +
                " WHERE m.type = :missionType" +
                " ORDER BY random()", Mission.class)
        .setParameter("missionType", missionType)
        .getResultStream()
        .findFirst();
  }
}
