package com.firefighter.aenitto.auth.repository;

import com.firefighter.aenitto.auth.domain.RefreshToken;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@Qualifier(value = "RefreshTokenRepositoryImpl")
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

  private final EntityManager em;

  @Override
  public RefreshToken saveRefreshToken(RefreshToken refreshToken) {
    em.persist(refreshToken);
    return refreshToken;
  }

  @Override
  public Optional<RefreshToken> findByMemberId(UUID memberId) {
    return em.createQuery("SELECT r FROM RefreshToken r WHERE r.memberId = :memberId", RefreshToken.class)
        .setParameter("memberId", memberId)
        .getResultList().stream().findFirst();
  }

  @Override
  public RefreshToken findRefreshTokenById(Long id) {
    return em.find(RefreshToken.class, id);
  }

  @Override
  public void deleteByMemberId(final UUID memberId) {
    em.createQuery("DELETE FROM RefreshToken r WHERE r.memberId = :memberId")
        .setParameter("memberId", memberId)
        .executeUpdate();
    ;
  }
}
