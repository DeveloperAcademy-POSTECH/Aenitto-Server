package com.firefighter.aenitto.rooms.domain;

import lombok.Getter;

@Getter
public enum RoomState implements Comparable<RoomState> {
    PRE(1), PROCESSING(1), POST(2)
    ;

    private int priority;
    public String toString() {
        switch (this) {
            case PRE:
                return "PRE";
            case POST:
                return "POST";
            case PROCESSING:
                return "PROCESSING";
            default:
                return null;
        }
    }
    RoomState(int priority) {
        this.priority = priority;
    }
}
