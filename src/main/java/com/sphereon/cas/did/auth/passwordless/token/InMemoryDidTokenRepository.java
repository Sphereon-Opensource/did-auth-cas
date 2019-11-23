package com.sphereon.cas.did.auth.passwordless.token;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class InMemoryDidTokenRepository implements DidTokenRepository {
    private final int tokenExpirationInSeconds;
    private static final int INITIAL_CACHE_SIZE = 50;
    private static final long MAX_CACHE_SIZE = 100_000_000;

    private final LoadingCache<String, DidToken> storage;

    public InMemoryDidTokenRepository(final int tokenExpirationInSeconds) {
        this.tokenExpirationInSeconds = tokenExpirationInSeconds;
        this.storage = Caffeine.newBuilder()
                .initialCapacity(INITIAL_CACHE_SIZE)
                .maximumSize(MAX_CACHE_SIZE)
                .expireAfterWrite(tokenExpirationInSeconds, TimeUnit.SECONDS)
                .build(s -> {
                    System.out.println("Load operation of the cache is not supported.");
                    return null;
                });
    }

    @Override
    public DidToken createToken(String username, String requestToken){
        return new DidToken(username, requestToken);
    }

    @Override
    public Optional<DidToken> findToken(final String username){
        return Optional.ofNullable(this.storage.getIfPresent(username));
    }

    @Override
    public void deleteToken(final String username){
        this.storage.invalidate(username);
    }

    @Override
    public void saveToken(final String username, final DidToken didToken){
        this.storage.put(username, didToken);
    }

    @Override
    public void updateToken(final String username, final String request, final String response){
        deleteToken(username);
        saveToken(username, new DidToken(username, request, response));
    }
}
