package com.firefighter.aenitto.common.response;

import lombok.Getter;

@Getter
public enum LinkRelation {
    FIRST("first"), NEXT("next")
    ;

    private String value;

    LinkRelation(String value) {
        this.value = value;
    }
}
