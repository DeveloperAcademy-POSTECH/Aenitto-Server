package com.firefighter.aenitto.auth.exception;

public class MemberNotFoundException extends RuntimeException{
    public MemberNotFoundException(String socialId){
        super(socialId + " NotFoundException");
    }
}
