package com.firefighter.aenitto.members.domain;

import com.firefighter.aenitto.common.baseEntities.CreationModificationLog;
import com.firefighter.aenitto.rooms.domain.MemberRoom;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends CreationModificationLog {

    @Id @GeneratedValue(generator = "uuid2")
//    @GenericGenerator(name = "uuid2", strategy = "uuid2")
//    @Type(type = "pg-uuid")
    @Column(name = "member_id", columnDefinition = "uuid")
    private UUID id;

    private String nickname;

    private String socialId;

    @OneToMany(mappedBy = "member")
    private List<MemberRoom> memberRooms = new ArrayList<>();

    @Builder
    public Member(UUID id, String nickname, String socialId) {
        this.id = id;
        this.nickname = nickname;
        this.socialId = socialId;
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }
}
