package com.sphereon.cas.did.auth.passwordless.token;

import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

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
        assert(didTokenOptional.isPresent());
         assertEquals(didTokenOptional.get().getRequestToken(), "testUserRequest");
    }

    @Test
    public void didTokenRepositoryDeletesTokens(){
        didTokenRepository.saveToken("testUserDelete", new DidToken("testUserDeleteRequest"));
        didTokenRepository.deleteToken("testUserDelete");
        assert(didTokenRepository.findToken("testUserDelete").isEmpty());
    }
}
