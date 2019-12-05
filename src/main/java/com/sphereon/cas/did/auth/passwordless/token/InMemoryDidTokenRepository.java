package com.sphereon.cas.did.auth.passwordless.token;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
public class InMemoryDidTokenRepository implements DidTokenRepository {
    private static final int INITIAL_CACHE_SIZE = 50;
    private static final long MAX_CACHE_SIZE = 100_000_000;

    private final Cache<String, DidToken> storage;

    public InMemoryDidTokenRepository(final int tokenExpirationInSeconds) {
        this.storage = Caffeine.newBuilder()
                .initialCapacity(INITIAL_CACHE_SIZE)
                .maximumSize(MAX_CACHE_SIZE)
                .expireAfterWrite(tokenExpirationInSeconds, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public Optional<DidToken> findToken(final String username) {
        return Optional.ofNullable(this.storage.getIfPresent(username));
    }

    @Override
    public void deleteToken(final String username) {
        this.storage.invalidate(username);
    }

    @Override
    public void saveToken(final String username, final DidToken didToken) {
        deleteToken(username);
        this.storage.put(username, didToken);
    }
}
