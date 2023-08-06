package com.firefighter.aenitto.members.domain;

import com.firefighter.aenitto.common.baseEntities.CreationModificationLog;
import com.firefighter.aenitto.rooms.domain.MemberRoom;
import com.firefighter.aenitto.rooms.domain.Room;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

  @ColumnDefault("false")
  private boolean withdrawal;

  @OneToMany(mappedBy = "member", orphanRemoval = true, cascade = CascadeType.ALL)
  private List<MemberRoom> memberRooms = new ArrayList<>();

  @Builder
  public Member(String nickname, String socialId, String fcmToken) {
    this.nickname = nickname;
    this.socialId = socialId;
    this.fcmToken = fcmToken;
  }

  public void withdrawal() {
    this.withdrawal = true;
    this.leaveRooms();
  }

  public void recovery() {
    this.withdrawal = false;
  }

  public void changeNickname(String nickname) {
    this.nickname = nickname;
  }

  public void setFcmToken(String fcmToken) {
    this.fcmToken = fcmToken;
  }

  public void removeMemberRoom(MemberRoom memberRoom) {
    memberRooms.remove(memberRoom);
  }

  private void leaveRooms() {
    this.getMemberRooms()
      .forEach(memberRoom -> {
        Room room = memberRoom.getRoom();
        room.kickOut(memberRoom);
      });
    this.memberRooms.clear();
  }
}
