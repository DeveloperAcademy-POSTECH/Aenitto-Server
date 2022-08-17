package com.firefighter.aenitto.common.utils;

import com.firefighter.aenitto.rooms.domain.Room;
import com.firefighter.aenitto.rooms.domain.RoomState;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RoomComparator {
    public static List<Room> sortRooms(List<Room> rooms) {
        return rooms.stream()
                .sorted(Comparator
                        .comparing(Room::getState, RoomComparator::compareState)
//                        .thenComparing(Room::getId, RoomComparator::compareId)
                ).collect(Collectors.toList());
    }

    private static int compareState(RoomState o1, RoomState o2) {
        return o1.getPriority() - o2.getPriority();
    }

    private static int compareId(Long o1, Long o2) {
        return o2.compareTo(o1);
    }
}
