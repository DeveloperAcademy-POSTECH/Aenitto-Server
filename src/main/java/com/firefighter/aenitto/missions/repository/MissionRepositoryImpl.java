package com.firefighter.aenitto.missions.repository;

import com.firefighter.aenitto.missions.domain.Mission;
import com.firefighter.aenitto.missions.domain.MissionType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("missionRepositoryImpl")
@RequiredArgsConstructor
public class MissionRepositoryImpl implements MissionRepository {
    private final EntityManager em;

    @Override
    public Optional<Mission> findRandomMission(MissionType missionType) {
        List<Mission> missionList = em.createQuery(
                        "SELECT m" +
                                " FROM Mission m" +
                                " WHERE m.type = :missionType" +
                                " ORDER BY random()", Mission.class)
                .setParameter("missionType", missionType)
                .setMaxResults(1)
                .getResultList();
        return missionList.isEmpty() ? Optional.empty() : Optional.of(missionList.get(0));
    }
}
