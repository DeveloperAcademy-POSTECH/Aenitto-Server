package com.firefighter.aenitto.rooms.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

public class RoomTest {
    @DisplayName("6개의 난수 (0-9 혹은 A-Z의 upper-case) 생성")
    @Test
    public void createRandomSixNumString() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // given
        Room room = Room.builder().build();
        Method randomSixNumUpperString = Room.class.getDeclaredMethod("randomSixNumUpperString");
        randomSixNumUpperString.setAccessible(true);

        // when
        String random = (String) randomSixNumUpperString.invoke(room);

        // then
        assertThat(random.length()).isEqualTo(6);
        assertThat(random).isUpperCase();
    }

//    @DisplayName("Room::isExpired 테스트 - 성공")
//    @Test
//    void isExpired_success() {
//        // given
//        Room room1 = Room.builder()
//                .endDate(LocalDate.of(2022, 9, 3))
//                .build();
//
//        Room room2 = Room.builder()
//                .endDate(LocalDate.of(2022, 9, 4))
//                .build();
//
//        Room room3 = Room.builder()
//                .endDate(LocalDate.of(2022, 9, 5))
//                .build();
//
//
//        // when, then
//        assertThat(room1.isExpired()).isTrue();
//        assertThat(room2.isExpired()).isFalse();
//        assertThat(room3.isExpired()).isFalse();
//    }
}

