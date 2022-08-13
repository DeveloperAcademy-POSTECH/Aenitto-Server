package com.firefighter.aenitto.message;

import com.firefighter.aenitto.messages.domain.Message;
import org.springframework.test.util.ReflectionTestUtils;

public class MessageFixture {
    public static Message messageFixture1() {
        return baseMessageFixture(1, true);
    }

    public static Message messageFixture2() {
        return baseMessageFixture(2, true);
    }

    public static Message messageFixture3() {
        return baseMessageFixture(3, true);
    }

    public static Message messageFixture4() {
        return baseMessageFixture(4, false);
    }

    public static Message messageFixture5() {
        return baseMessageFixture(5, false);
    }

    public static Message messageFixture6() {
        return baseMessageFixture(6, false);
    }

    public static Message messageFixture7() {
        return baseMessageFixture(7, false);
    }


    private static Message baseMessageFixture(int number, boolean read) {
        Message message = transientMessageFixture(number);
        ReflectionTestUtils.setField(message, "id", number * 1L);
        ReflectionTestUtils.setField(message, "read", read);
        return message;
    }

    public static Message transientMessageFixture(int number) {
        return Message.builder()
                .content("메시지내용" + number)
                .imgUrl("url" + number)
                .build();
    }
}