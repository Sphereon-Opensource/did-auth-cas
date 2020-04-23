package com.sphereon.cas.did.auth.passwordless.repository;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
public class InMemoryRegsitrationQRCodeRepository implements RegistrationQRCodeRepository {
    private static final int INITIAL_CACHE_SIZE = 50;
    private static final long MAX_CACHE_SIZE = 1000;

    private final Cache<String, String> storage;

    public InMemoryRegsitrationQRCodeRepository(final int tokenExpirationInSeconds) {
        this.storage = Caffeine.newBuilder()
                .initialCapacity(INITIAL_CACHE_SIZE)
                .maximumSize(MAX_CACHE_SIZE)
                .expireAfterWrite(tokenExpirationInSeconds, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public Optional<String> findQR(final String topicId) {
        return Optional.ofNullable(this.storage.getIfPresent(topicId));
    }

    @Override
    public void deleteQR(final String topicId) {
        this.storage.invalidate(topicId);
    }

    @Override
    public void saveQR(final String topicId, final String qrCodeBase64) {
        deleteQR(topicId);
        this.storage.put(topicId, qrCodeBase64);
    }
}
