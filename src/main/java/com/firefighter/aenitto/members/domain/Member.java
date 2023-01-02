package com.firefighter.aenitto.members.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.*;

import com.firefighter.aenitto.common.baseEntities.CreationModificationLog;
import com.firefighter.aenitto.rooms.domain.MemberRoom;

import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends CreationModificationLog {

	@Id
	@GeneratedValue(generator = "uuid2")
	//    @GenericGenerator(name = "uuid2", strategy = "uuid2")
	//    @Type(type = "pg-uuid")
	@Column(name = "member_id", columnDefinition = "uuid")
	private UUID id;

	private String nickname;

	private String socialId;

	private String fcmToken;

	@OneToMany(mappedBy = "member")
	private List<MemberRoom> memberRooms = new ArrayList<>();

	@Builder
	public Member(String nickname, String socialId, String fcmToken) {
		this.nickname = nickname;
		this.socialId = socialId;
		this.fcmToken = fcmToken;
	}

	public void changeNickname(String nickname) {
		this.nickname = nickname;
	}

	public void setFcmToken(String fcmToken) {
		this.fcmToken = fcmToken;
	}
}
