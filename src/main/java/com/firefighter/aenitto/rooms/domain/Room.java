package com.firefighter.aenitto.rooms.domain;

import com.firefighter.aenitto.common.baseEntities.CreationModificationLog;
import com.firefighter.aenitto.common.utils.DateConverter;
import com.firefighter.aenitto.rooms.dto.request.UpdateRoomRequest;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;


import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Entity
@Getter
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Room extends CreationModificationLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long id;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<MemberRoom> memberRooms = new ArrayList<>();

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<Relation> relations = new ArrayList<>();

    private String title;
    private int capacity;

    private String invitation;

    @Enumerated(value = EnumType.STRING)
    private RoomState state = RoomState.PRE;

    @ColumnDefault(value = "false")
    private boolean deleted;

    private LocalDate startDate;

    private LocalDate endDate;

    @Builder
    public Room(String title, int capacity, String invitation, LocalDate startDate, LocalDate endDate) {
        this.title = title;
        this.capacity = capacity;
        this.invitation = invitation;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void createInvitation() {
        this.invitation = randomSixNumUpperString();
    }

    public void setState(RoomState state) {
        this.state = state;
    }

    public void delete() {
        this.deleted = true;
    }

    public String getStartDateValue() {
        return DateConverter.localDateToString(this.startDate);
    }

    public String getEndDateValue() {
        return DateConverter.localDateToString(this.endDate);
    }

    public int participantsCount() {
        return memberRooms.size();
    }

    public boolean cannotStart() {
        return (5 > participantsCount());
    }
    public boolean unAcceptable() {
        return (capacity <= memberRooms.size());
    }

    public boolean isExpired() {
        return this.endDate.isBefore(LocalDate.now());
    }

    public Room updateRoom(UpdateRoomRequest request) {
        if (request.getTitle() != null) {
            this.title = request.getTitle();
        }
        if (request.getCapacity() != null) {
            this.capacity = request.getCapacity();
        }
        if (request.getStartDate() != null) {
            this.startDate = DateConverter.stringToLocalDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            this.endDate = DateConverter.stringToLocalDate(request.getEndDate());
        }
        return this;
    }

    private String randomSixNumUpperString() {
        Random random = new Random();
        return random.ints(48, 91)
                .filter((rand) -> (rand < 58) || (rand >= 65))
                .limit(6)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
