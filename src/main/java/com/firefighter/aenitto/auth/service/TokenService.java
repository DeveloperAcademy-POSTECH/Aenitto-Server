package com.firefighter.aenitto.auth.service;

import com.firefighter.aenitto.auth.token.CurrentUserDetails;
import com.firefighter.aenitto.auth.token.Token;
import com.firefighter.aenitto.common.exception.auth.InvalidTokenException;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class TokenService {
  private String secretKey = "tokenSecretKeyForAenittoServiceForFireFighter";

  private static final String AUTHORITIES_KEY = "role";

  private final UserDetailsService userDetailsService;

  @PostConstruct
  protected void init() {
    secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
  }

  public Token generateToken(String uid, String role) {
    long tokenPeriod = 1000L * 60L * 60L * 24L * 30L * 12L;
    long refreshPeriod = 1000L * 60L * 60L * 24L * 30L * 12L;
    System.out.println("generating token");

    Claims claims = Jwts.claims().setSubject(uid);
    claims.put("role", role);

    Date now = new Date();
    return new Token(Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(new Date(now.getTime() + tokenPeriod))
        .signWith(SignatureAlgorithm.HS256, secretKey)
        .compact(),

        Jwts.builder()
            .setExpiration(new Date(now.getTime() + refreshPeriod))
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact());
  }

  public String generateAccessToken(String uid, String role) {
    long tokenPeriod = 1000L * 60L * 60L * 3L;

    Claims claims = Jwts.claims().setSubject(uid);
    claims.put("role", role);

    Date now = new Date();

    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(new Date(now.getTime() + tokenPeriod))
        .signWith(SignatureAlgorithm.HS256, secretKey)
        .compact();
  }

  public long verifyRefreshToken(String token) {
    try {
      Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
      return claims.getBody().getExpiration().getTime();
    } catch (Exception e) {
      throw new InvalidTokenException();
    }
  }

  public boolean verifyToken(String token) {
    try {
      Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
      return claims.getBody().getExpiration().after(new Date());
    } catch (Exception e) {
      return false;
    }
  }

  public boolean checkTokenExpired(String token) {
    try {
      Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
      Long time = claims.getBody().getExpiration().getTime();
      System.out.println("토큰 남은 기한 " + time);
      if (time > 1L) {
        return true;
      } else {
        return false;
      }
    } catch (Exception e) {
      throw new InvalidTokenException();
    }

  }

  public String getUid(String token) {
    return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
  }

  public Authentication getAuthentication(String accessToken) {
    Claims claims = parseClaims(accessToken);

    if (claims.get(AUTHORITIES_KEY) == null) {
      throw new RuntimeException("권한 정보가 없는 토큰입니다.");
    }
    final String socialId = claims.getSubject();
    final CurrentUserDetails currentUserDetails = (CurrentUserDetails) userDetailsService.loadUserByUsername(
        socialId);

    return new UsernamePasswordAuthenticationToken(currentUserDetails, "", currentUserDetails.getAuthorities());
  }

  public String getSocialId(String accessToken) {
    Claims claims = parseClaims(accessToken);

    if (claims.get(AUTHORITIES_KEY) == null) {
      System.out.println("권한 정보가 없는 토큰입니다");
      throw new RuntimeException("권한 정보가 없는 토큰입니다.");
    }
    final String socialId = claims.getSubject();
    return socialId;
  }

  private Claims parseClaims(String accessToken) {
    try {
      return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(accessToken).getBody();
    } catch (ExpiredJwtException e) {
      throw new InvalidTokenException();
    } catch (io.jsonwebtoken.security.SignatureException e) {
      throw new InvalidTokenException();
    }
  }
}
