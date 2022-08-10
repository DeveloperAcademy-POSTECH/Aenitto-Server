package com.firefighter.aenitto.rooms.domain;

public enum RoomState {
    PRE, PROCESSING, POST
    ;

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
}
