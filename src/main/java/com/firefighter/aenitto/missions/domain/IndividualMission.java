package com.firefighter.aenitto.missions.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.*;

import org.hibernate.annotations.ColumnDefault;

import com.firefighter.aenitto.rooms.domain.MemberRoom;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IndividualMission {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "individual_mission_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mission_id")
	private Mission mission;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_room_id")
	private MemberRoom memberRoom;

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

	public void setMemberRoom(MemberRoom memberRoom) {
		this.memberRoom = memberRoom;
	}

	public static IndividualMission of(Mission mission, LocalDate date) {
		return new IndividualMission(date, mission);
	}
}

