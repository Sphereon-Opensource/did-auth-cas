package com.sphereon.cas.did.auth.passwordless.callback;

import com.sphereon.cas.did.auth.passwordless.callback.model.CallbackTokenPostRequest;
import com.sphereon.cas.did.auth.passwordless.repository.model.DidToken;
import com.sphereon.cas.did.auth.passwordless.repository.DidTokenRepository;
import com.sphereon.cas.did.auth.passwordless.repository.InMemoryDidTokenRepository;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CallbackEndpointControllerTest {
    private DidTokenRepository didTokenRepository;
    private CallbackEndpointController callbackEndpointController;

    @Before
    public void setUp(){
        this.didTokenRepository = new InMemoryDidTokenRepository(60);
        this.callbackEndpointController = new CallbackEndpointController(didTokenRepository);
    }

    @Test
    public void callbackEndpointShouldUpdateToken(){
        var testUsername = "testUsername";

        didTokenRepository.saveToken(testUsername, new DidToken("requestToken"));
        CallbackTokenPostRequest callbackTokenPostRequest = new CallbackTokenPostRequest("testResponseToken");
        callbackEndpointController.postLoginToken(testUsername, callbackTokenPostRequest);

        Optional<DidToken> testUserTokenOptional = didTokenRepository.findToken(testUsername);

        assertTrue(testUserTokenOptional.isPresent());
        DidToken testUserToken = testUserTokenOptional.get();
        assertTrue(testUserToken.isResponseReceived());
        assertEquals(testUserToken.getResponseToken(), "testResponseToken");
    }
}
