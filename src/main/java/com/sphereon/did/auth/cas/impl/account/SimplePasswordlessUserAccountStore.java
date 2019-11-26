package com.sphereon.did.auth.cas.impl.account;

import com.sphereon.did.auth.cas.api.PasswordlessUserAccount;
import com.sphereon.did.auth.cas.api.PasswordlessUserAccountStore;

import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;

/**
 * This is {@link SimplePasswordlessUserAccountStore}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@RequiredArgsConstructor
public class SimplePasswordlessUserAccountStore implements PasswordlessUserAccountStore {
    private final Map<String, PasswordlessUserAccount> accounts;

    @Override
    public Optional<PasswordlessUserAccount> findUser(final String username) {
        return Optional.empty();
    }
}
