package com.firefighter.aenitto.rooms.dto.response;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.firefighter.aenitto.common.response.PaginatedResponse;
import com.firefighter.aenitto.rooms.domain.Room;

@Getter
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class ParticipatingRoomsResponse implements PaginatedResponse {
	private final List<ParticipatingRoom> participatingRooms;

	public static ParticipatingRoomsResponse of(List<Room> rooms) {
		return new ParticipatingRoomsResponse(rooms.stream().map(ParticipatingRoom::of).collect(Collectors.toList()));
	}

	@Getter
	@NoArgsConstructor(force = true)
	@AllArgsConstructor
	@Builder
	public static class ParticipatingRoom {
		private final Long id;
		private final String title;
		private final String state;
		private final int participatingCount;
		private final int capacity;
		private final String startDate;
		private final String endDate;

		public static ParticipatingRoom of(Room room) {
			return ParticipatingRoom.builder()
				.id(room.getId())
				.title(room.getTitle())
				.state(room.getState().toString())
				.participatingCount(room.getMemberRooms().size())
				.capacity(room.getCapacity())
				.startDate(room.getStartDateValue())
				.endDate(room.getEndDateValue())
				.build();
		}
	}

	@Override
	public Optional<String> nextCursor() {
		Long cursor;
		try {
			cursor = this.participatingRooms.get(this.participatingRooms.size() - 1).getId();
		} catch (IndexOutOfBoundsException e) {
			cursor = null;
		}
		return Optional.ofNullable((cursor != null) ? String.valueOf(cursor) : null);
	}

	@Override
	public int pageCount() {
		return this.participatingRooms.size();
	}
}
