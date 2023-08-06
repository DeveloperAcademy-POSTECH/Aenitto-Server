package com.firefighter.aenitto.rooms.domain;

import com.firefighter.aenitto.members.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Relation {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "relation_id")
  private Long id;

  /*
  Manitto: 챙겨주는 사람
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "manitto_id")
  private Member manitto;

  /*
  Manittee : 챙김 받는 사람
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "manittee_id")
  private Member manittee;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "room_id")
  private Room room;

  private static void createRelations(Member manitto, Member manittee, Room room) {
    Relation relation = new Relation(manitto, manittee, room);
    room.getRelations().add(relation);
  }

  private Relation(Member manitto, Member manittee, Room room) {
    this.manitto = manitto;
    this.manittee = manittee;
    this.room = room;
  }

  public static void createRelations(Room room) {
    List<MemberRoom> memberRooms = room.getMemberRooms();
    final int size = memberRooms.size();
    final List<Integer> nums = new ArrayList<>(size);

    for (int i = 0; i < size; i++) {
      nums.add(i);
    }
    final Set<Integer> left = new HashSet<>(nums);
    final Deque<Integer> deque = new ArrayDeque<>();
    int count = 0;
    Random random = new Random();

    while (deque.size() < size) {
      if (left.size() == 1 && left.contains(count)) {
        left.add(deque.pollLast());
        count--;
      }
      int randomNum = random.nextInt(size);
      if (left.contains(randomNum) && randomNum != count) {
        deque.addLast(randomNum);
        left.remove(randomNum);
        count++;
      }
    }

    count = 0;
    while (!deque.isEmpty()) {
      createRelations(memberRooms.get(deque.pollFirst()).getMember(), memberRooms.get(count).getMember(), room);
      count++;
    }
  }
}
