package com.sphereon.cas.did.auth.passwordless;

import java.util.Optional;

@FunctionalInterface
public interface PassworldessUserAccountStore {
    Optional<PasswordlessUserAccount> findUser(String username);
}
