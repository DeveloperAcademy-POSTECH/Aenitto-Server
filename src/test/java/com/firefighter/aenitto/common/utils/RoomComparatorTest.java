package com.firefighter.aenitto.common.utils;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.firefighter.aenitto.rooms.RoomFixture;
import com.firefighter.aenitto.rooms.domain.Room;
import com.firefighter.aenitto.rooms.domain.RoomState;

public class RoomComparatorTest {

	@DisplayName("Room sort 테스트")
	@Test
	void compareTo() {
		// given
		Room room1 = RoomFixture.transientRoomFixture(1, 10, 10);
		Room room2 = RoomFixture.transientRoomFixture(2, 10, 10);
		Room room3 = RoomFixture.transientRoomFixture(3, 10, 10);
		Room room4 = RoomFixture.transientRoomFixture(4, 10, 10);
		Room room5 = RoomFixture.transientRoomFixture(5, 10, 10);
		Room room6 = RoomFixture.transientRoomFixture(6, 10, 10);

		room1.setState(RoomState.PRE);
		room2.setState(RoomState.PRE);
		room3.setState(RoomState.POST);
		room4.setState(RoomState.PROCESSING);
		room5.setState(RoomState.POST);
		room6.setState(RoomState.PRE);

		ReflectionTestUtils.setField(room1, "id", 1L);
		ReflectionTestUtils.setField(room2, "id", 2L);
		ReflectionTestUtils.setField(room3, "id", 3L);
		ReflectionTestUtils.setField(room4, "id", 4L);
		ReflectionTestUtils.setField(room5, "id", 5L);
		ReflectionTestUtils.setField(room6, "id", 6L);

		List<Room> rooms = Arrays.asList(room6, room5, room4, room3, room2, room1);

		// when
		List<Room> sortedRooms = RoomComparator.sortRooms(rooms);

		// then
		assertThat(sortedRooms.get(0).getId()).isEqualTo(4L);
		assertThat(sortedRooms.get(1).getId()).isEqualTo(6L);
		assertThat(sortedRooms.get(2).getId()).isEqualTo(2L);
		assertThat(sortedRooms.get(3).getId()).isEqualTo(1L);
		assertThat(sortedRooms.get(4).getId()).isEqualTo(5L);
		assertThat(sortedRooms.get(5).getId()).isEqualTo(3L);
	}
}
