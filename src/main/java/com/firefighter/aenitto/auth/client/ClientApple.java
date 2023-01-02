package com.firefighter.aenitto.auth.client;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.firefighter.aenitto.auth.client.dto.ApplePublicKeyResponse;
import com.firefighter.aenitto.common.exception.auth.FailedToFetchPublicKeyException;
import com.firefighter.aenitto.common.exception.auth.InvalidIdentityTokenException;

@RequiredArgsConstructor
@Slf4j
@Component
public class ClientApple implements ClientProxy {
	@Autowired
	private final WebClient webClient;

	public ApplePublicKeyResponse getAppleAuthPublicKey() {
		ApplePublicKeyResponse applePublicKeyResponse = webClient.get()
			.uri("https://appleid.apple.com/auth/keys")
			.retrieve()
			.bodyToMono(ApplePublicKeyResponse.class)
			.block();
		return applePublicKeyResponse;
	}

	@Override
	public String validateToken(String identityToken) {
		try {
			ApplePublicKeyResponse applePublicKeyResponse = getAppleAuthPublicKey();
			String headerOfIdentityToken = identityToken.substring(0, identityToken.indexOf("."));
			Map<String, String> header = new ObjectMapper().readValue(
				new String(Base64.getDecoder().decode(headerOfIdentityToken),
					"UTF-8"), Map.class);
			ApplePublicKeyResponse.Key key = applePublicKeyResponse.getMatchedKeyBy(header.get("kid"),
					header.get("alg"))
				.orElseThrow(() -> new FailedToFetchPublicKeyException());

			byte[] nBytes = Base64.getUrlDecoder().decode(key.getN());
			byte[] eBytes = Base64.getUrlDecoder().decode(key.getE());

			BigInteger n = new BigInteger(1, nBytes);
			BigInteger e = new BigInteger(1, eBytes);

			RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(n, e);
			KeyFactory keyFactory = KeyFactory.getInstance(key.getKty());
			PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

			Claims claims = Jwts.parserBuilder().setSigningKey(publicKey)
				.build().parseClaimsJws(identityToken).getBody();

			JsonObject userInfoObject = new Gson().fromJson(new Gson().toJson(claims), JsonObject.class);
			JsonElement appleAlg = userInfoObject.get("sub");
			String appleSocialId = appleAlg.getAsString();

			return appleSocialId;

		} catch (JsonMappingException e) {
			log.error(e.getMessage());
			throw new InvalidIdentityTokenException();
		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage());
			throw new InvalidIdentityTokenException();
		} catch (JsonProcessingException e) {
			log.error(e.getMessage());
			throw new InvalidIdentityTokenException();
		} catch (NoSuchAlgorithmException e) {
			log.error(e.getMessage());
			throw new InvalidIdentityTokenException();
		} catch (InvalidKeySpecException e) {
			log.error(e.getMessage());
			throw new InvalidIdentityTokenException();
		} catch (MalformedJwtException e) {
			throw new InvalidIdentityTokenException();
		}
	}
}
