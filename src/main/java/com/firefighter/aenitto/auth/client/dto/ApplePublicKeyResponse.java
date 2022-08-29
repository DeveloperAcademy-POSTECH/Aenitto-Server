package com.firefighter.aenitto.auth.client.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@NoArgsConstructor(force = true)
public class ApplePublicKeyResponse {
    private final List<Key> keys;

    @Getter
    @NoArgsConstructor(force = true)
    public static class Key {
        private final String kty;
        private final String kid;
        private final String use;
        private final String alg;
        private final String n;
        private final String e;
    }

    public Optional<Key> getMatchedKeyBy(String kid, String alg) {
        return this.keys.stream()
                .filter(key -> key.getKid().equals(kid) && key.getAlg().equals(alg))
                .findFirst();
    }
}
