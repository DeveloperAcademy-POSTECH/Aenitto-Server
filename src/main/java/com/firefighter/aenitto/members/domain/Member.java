package com.firefighter.aenitto.members.domain;

import com.firefighter.aenitto.common.baseEntities.CreationModificationLog;
import com.firefighter.aenitto.rooms.domain.MemberRoom;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

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
  private boolean withdrawl;

  @OneToMany(mappedBy = "member", orphanRemoval = true)
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

  public void withdrawl(boolean isWithdrawl) {
    this.withdrawl = isWithdrawl;
  }
}
