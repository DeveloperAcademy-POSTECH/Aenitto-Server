package com.firefighter.aenitto.rooms.domain;

import com.firefighter.aenitto.common.baseEntities.CreationModificationLog;
import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.missions.domain.IndividualMission;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberRoom extends CreationModificationLog {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "member_room_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @OneToMany(mappedBy = "memberRoom")
    private List<IndividualMission> individualMissions = new ArrayList<>();

    private boolean admin;

    @ColumnDefault(value = "false")
    private boolean viewManitto;

    @Column
    private int colorIdx;

    @Enumerated(value = EnumType.STRING)
    private ParticipantRole role;

    @Builder
    public MemberRoom(boolean admin, int colorIdx) {
        this.admin = admin;
        this.colorIdx = colorIdx;
    }

    public boolean isAdmin() {
        return this.admin;
    }

    public boolean didViewManitto() {
        return this.viewManitto;
    }

    public void setViewManito() {
        viewManitto = true;
    }
}

