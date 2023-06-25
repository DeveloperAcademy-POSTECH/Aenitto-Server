package com.firefighter.aenitto.missions.repository;

import java.time.LocalDate;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import com.firefighter.aenitto.missions.domain.IndividualMission;
import com.firefighter.aenitto.missions.domain.Mission;
import com.firefighter.aenitto.missions.domain.MissionType;

@Repository
@Qualifier("missionRepositoryImpl")
@RequiredArgsConstructor
public class MissionRepositoryImpl implements MissionRepository {
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

	@Override
	public Optional<IndividualMission> findIndividualMissionByDate(LocalDate date, Long memberRoomId) {
		return em.createQuery(
				"SELECT im" +
					" FROM IndividualMission im" +
					" WHERE im.date = :date" +
					" AND im.memberRoom.id = :memberRoomId", IndividualMission.class)
			.setParameter("date", date)
			.setParameter("memberRoomId", memberRoomId)
			.getResultStream()
			.findFirst();
	}

	@Override
	public Optional<Mission> findById(Long id) {
		return Optional.ofNullable(em.find(Mission.class, id));
	}

	@Override
	public void save(Mission mission) {
		em.persist(mission);
	}
}
