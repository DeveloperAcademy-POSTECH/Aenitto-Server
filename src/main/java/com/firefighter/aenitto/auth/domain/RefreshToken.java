package com.firefighter.aenitto.auth.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "token_id")
    private Long id;

    /*
    실제 mapping x
     */
//    @Type(type="pg-uuid")
    @Column(columnDefinition = "uuid")
    private UUID memberId;

    @Column
    private String refreshToken;

    @Builder
    public RefreshToken(UUID memberId, String refreshToken) {
        this.memberId = memberId;
        this.refreshToken = refreshToken;
    }
}
