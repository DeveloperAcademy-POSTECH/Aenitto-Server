package com.firefighter.aenitto.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.firefighter.aenitto.auth.domain.RefreshToken;

@Repository
public interface RefreshTokenRepository {
	RefreshToken saveRefreshToken(final RefreshToken refreshToken);

	Optional<RefreshToken> findByMemberId(final UUID memberId);

	RefreshToken findRefreshTokenById(Long id);

	void deleteByMemberId(final UUID memberId);
}
