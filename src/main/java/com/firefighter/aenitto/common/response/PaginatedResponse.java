package com.firefighter.aenitto.common.response;

import java.util.Optional;

// TODO: PaginatedResponse refactor - 각 구현체에서 구현하지 않도록 (JsonProperty 로 설정해 줌) (22.08.07)
public interface PaginatedResponse {
	public Optional<String> nextCursor();

	public int pageCount();
}
