package com.firefighter.aenitto.messages.domain;


import com.firefighter.aenitto.common.baseEntities.CreationLog;
import com.firefighter.aenitto.members.domain.Member;
import com.firefighter.aenitto.rooms.domain.Relation;
import com.firefighter.aenitto.rooms.domain.Room;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message extends CreationLog {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "message_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sender_id")
  private Member sender;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "receiver_id")
  private Member receiver;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "room_id")
  private Room room;

  @OneToOne(mappedBy = "message", cascade = CascadeType.ALL)
  private Report report;

  private String content;

  @ColumnDefault(value = "false")
  private boolean read;

  @Column
  private String imgUrl;

  @Column
  private Long missionId;

  public void sendMessage(Member sender, Member receiver, Room room) {
    this.sender = sender;
    this.receiver = receiver;
    this.room = room;
  }

  public void setMessageRelationship(Relation relation) {
    this.sender = relation.getManitto();
    this.receiver = relation.getManittee();
    this.room = relation.getRoom();
  }

  @Builder
  public Message(String content, String imgUrl, Long missionId) {
    this.content = content;
    this.imgUrl = imgUrl;
    this.missionId = missionId;
  }

  public static Message initializeMessageRelationship(String content, Relation relation) {
    Message message = Message.builder().content(content).build();
    message.setMessageRelationship(relation);
    return message;
  }

  public void setImgUrl(String imgUrl) {
    this.imgUrl = imgUrl;
  }

  public void setMissionId(Long missionId) {
    this.missionId = missionId;
  }

  public boolean didRead() {
    return this.read;
  }

  public void readMessage() {
    this.read = true;
  }

  public boolean reportExists() {
    return report != null;
  }
}
