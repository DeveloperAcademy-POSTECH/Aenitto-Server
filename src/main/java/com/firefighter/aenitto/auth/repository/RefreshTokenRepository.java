package com.firefighter.aenitto.auth.repository;

import com.firefighter.aenitto.auth.domain.RefreshToken;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RefreshTokenRepository {
    RefreshToken saveRefreshToken(final RefreshToken refreshToken);
    RefreshToken findByMemberId(final UUID memberId);
    RefreshToken findRefreshTokenById(Long id);
}
