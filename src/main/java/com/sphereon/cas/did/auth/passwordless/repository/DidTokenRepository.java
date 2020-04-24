package com.sphereon.cas.did.auth.passwordless.repository;

import com.sphereon.cas.did.auth.passwordless.repository.model.DidToken;

import java.util.Optional;

public interface DidTokenRepository {

    Optional<DidToken> findToken(String username);

    void deleteToken(String username);

    void saveToken(String username, DidToken didToken);
}
