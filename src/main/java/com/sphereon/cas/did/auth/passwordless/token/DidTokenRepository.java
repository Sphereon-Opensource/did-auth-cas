package com.sphereon.cas.did.auth.passwordless.token;

import java.util.Optional;

public interface DidTokenRepository {
    DidToken createToken(String username, String request);

    void updateToken(String username, String request, String response);

    Optional<DidToken> findToken(String username);

    void deleteToken(String username);

    void saveToken(String username, DidToken didToken);
}
