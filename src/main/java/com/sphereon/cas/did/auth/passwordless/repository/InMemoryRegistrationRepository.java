package com.sphereon.cas.did.auth.passwordless.repository;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.sphereon.cas.did.auth.passwordless.repository.model.RegistrationRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
public class InMemoryRegistrationRepository implements RegistrationRepository {
    private static final int INITIAL_CACHE_SIZE = 50;
    private static final long MAX_CACHE_SIZE = 1000;

    private final Cache<String, RegistrationRequest> storage;

    public InMemoryRegistrationRepository(final int tokenExpirationInSeconds) {
        this.storage = Caffeine.newBuilder()
                .initialCapacity(INITIAL_CACHE_SIZE)
                .maximumSize(MAX_CACHE_SIZE)
                .expireAfterWrite(tokenExpirationInSeconds, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public Optional<RegistrationRequest> findRegistrationRequest(final String registrationId) {
        return Optional.ofNullable(this.storage.getIfPresent(registrationId));
    }

    @Override
    public void deleteRegistrationRequest(final String registrationId) {
        this.storage.invalidate(registrationId);
    }

    @Override
    public void saveRegistrationRequest(final String registrationId, final RegistrationRequest registrationRequest) {
        deleteRegistrationRequest(registrationId);
        this.storage.put(registrationId, registrationRequest);
    }
}
