package com.sphereon.cas.did.auth.passwordless.repository;

import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InMemoryDidTokenRepositoryTest {
    private InMemoryDidTokenRepository didTokenRepository;

    @Before
    public void setUp(){
        this.didTokenRepository = new InMemoryDidTokenRepository(60);
    }

    @Test
    public void didTokenRepositoryShouldPersistTokens() {
        didTokenRepository.saveToken("testUser", new DidToken("testUserRequest"));
        Optional<DidToken> didTokenOptional = didTokenRepository.findToken("testUser");
        assertTrue(didTokenOptional.isPresent());
         assertEquals(didTokenOptional.get().getRequestToken(), "testUserRequest");
    }

    @Test
    public void didTokenRepositoryDeletesTokens(){
        didTokenRepository.saveToken("testUserDelete", new DidToken("testUserDeleteRequest"));
        didTokenRepository.deleteToken("testUserDelete");
        assertTrue(didTokenRepository.findToken("testUserDelete").isEmpty());
    }
}
