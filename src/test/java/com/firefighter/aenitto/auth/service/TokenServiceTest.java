package com.firefighter.aenitto.auth.service;


import com.firefighter.aenitto.auth.token.Token;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class TokenServiceTest {

    private UUID testUUID = UUID.randomUUID();
    @InjectMocks
    private TokenService tokenService;

    @Test
    public void 토큰_생성하기() {
        Token token = tokenService.generateToken("UUID입니다","가나다라");
        System.out.println(">>>>>>>>>>>>>> token = " + token);
    }

    @Test
    public void access_토큰_검증하기() {
        String token = tokenService.generateAccessToken("UUID입니다","가나다라");
        System.out.println(token);
        tokenService.verifyToken(token);
    }

    @Test
    public void refresh_토큰_검증하기() {
        Token token = tokenService.generateToken("UUID입니다","가나다라");
        System.out.println(token);
        tokenService.verifyToken(token.getRefreshToken());
    }
}
