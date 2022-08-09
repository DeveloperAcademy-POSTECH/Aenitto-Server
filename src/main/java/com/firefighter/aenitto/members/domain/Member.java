package com.firefighter.aenitto.members.domain;

import com.firefighter.aenitto.common.baseEntities.CreationModificationLog;
import com.firefighter.aenitto.rooms.domain.MemberRoom;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

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

    @OneToMany(mappedBy = "member")
    private List<MemberRoom> memberRooms = new ArrayList<>();

    @Builder
    public Member(String nickname) {
        this.nickname = nickname;
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }
}
