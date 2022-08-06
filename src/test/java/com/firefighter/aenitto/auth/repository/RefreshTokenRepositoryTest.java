package com.firefighter.aenitto.auth.repository;

import com.firefighter.aenitto.auth.domain.RefreshToken;
import com.firefighter.aenitto.auth.token.Token;
import com.firefighter.aenitto.rooms.domain.Room;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ExtendWith(SpringExtension.class)
public class RefreshTokenRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    private RefreshTokenRepositoryImpl refreshTokenRepository;

    private RefreshToken refreshToken;

    @BeforeEach
    void setRefreshToken() {

        this.refreshToken = RefreshToken.builder()
                .memberId(UUID.fromString("b48617b2-090d-4ee6-9033-b99f99d98304"))
                .refreshToken("refreshToken입니다123123")
                .build();
    }

    @Test
    @DisplayName("RefreshToken 생성 테스트")
    public void refreshToken등록() {

        //when
        final RefreshToken result = refreshTokenRepository.saveRefreshToken(refreshToken);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getMemberId()).isEqualTo(UUID.fromString("b48617b2-090d-4ee6-9033-b99f99d98304"));
        assertThat(result.getRefreshToken()).isEqualTo("refreshToken입니다123123");
    }

    @Test
    @DisplayName("refreshToken 존재 여부 테스트")
    public void 사용자가이미존재하는지테스트() {

        // when
        final RefreshToken result = refreshTokenRepository.saveRefreshToken(refreshToken);

        final RefreshToken findResult = refreshTokenRepository.findByMemberId(result.getMemberId());

        // then
        assertThat(findResult.getId()).isNotNull();
        assertThat(findResult.getMemberId()).isEqualTo(UUID.fromString("b48617b2-090d-4ee6-9033-b99f99d98304"));
        assertThat(findResult.getRefreshToken()).isEqualTo("refreshToken입니다123123");
    }
}
