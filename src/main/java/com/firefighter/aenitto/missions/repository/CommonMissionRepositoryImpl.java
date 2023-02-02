package com.firefighter.aenitto.missions.repository;

import java.time.LocalDate;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import com.firefighter.aenitto.missions.domain.CommonMission;

@Repository
@Qualifier("commonMissionRepositoryImpl")
@RequiredArgsConstructor
public class CommonMissionRepositoryImpl implements CommonMissionRepository {
	private final EntityManager em;

	@Override
	public CommonMission saveCommonMission(CommonMission commonMission) {
		em.persist(commonMission);
		return commonMission;
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
