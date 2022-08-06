package com.firefighter.aenitto.auth.repository;

import com.firefighter.aenitto.auth.domain.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository{

    private final EntityManager em;

    @Override
    public RefreshToken saveRefreshToken(RefreshToken refreshToken) {
        em.persist(refreshToken);
        return refreshToken;
    }

    @Override
    public Optional<RefreshToken> findByMemberId(UUID memberId) {
        return Optional.ofNullable(em.createQuery("SELECT r FROM RefreshToken r WHERE r.memberId = :memberId", RefreshToken.class)
                .setParameter("memberId" , memberId)
                .getSingleResult());
    }

    @Override
    public RefreshToken findRefreshTokenById(Long id) {
        return em.find(RefreshToken.class, id);
    }
}
