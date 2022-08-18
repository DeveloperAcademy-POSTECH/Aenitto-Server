package com.firefighter.aenitto.missions.repository;

import com.firefighter.aenitto.missions.domain.CommonMission;
import com.firefighter.aenitto.missions.domain.Mission;
import com.firefighter.aenitto.missions.domain.MissionType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("missionRepositoryImpl")
@RequiredArgsConstructor
public class MissionRepositoryImpl implements MissionRepository {
    private final EntityManager em;

    @Override
    public CommonMission saveCommonMission(CommonMission commonMission) {
        em.persist(commonMission);
        return commonMission;
    }

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

    @Override
    public Optional<CommonMission> findCommonMissionByDate(LocalDate date) {
        return em.createQuery(
                        "SELECT c" +
                                " FROM CommonMission c" +
                                " WHERE c.date = :date", CommonMission.class)
                .setParameter("date", date)
                .getResultStream()
                .findFirst();
    }
}
