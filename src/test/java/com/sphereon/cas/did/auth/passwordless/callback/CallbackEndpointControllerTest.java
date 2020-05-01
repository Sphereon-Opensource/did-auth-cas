package com.sphereon.cas.did.auth.passwordless.callback;

import com.sphereon.cas.did.auth.passwordless.callback.model.CallbackTokenPostRequest;
import com.sphereon.cas.did.auth.passwordless.repository.InMemoryRegistrationRepository;
import com.sphereon.cas.did.auth.passwordless.repository.RegistrationRepository;
import com.sphereon.cas.did.auth.passwordless.repository.model.DidToken;
import com.sphereon.cas.did.auth.passwordless.repository.DidTokenRepository;
import com.sphereon.cas.did.auth.passwordless.repository.InMemoryDidTokenRepository;
import com.sphereon.libs.did.auth.client.DidAuthFlow;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class CallbackEndpointControllerTest {
    private DidTokenRepository didTokenRepository;
    private RegistrationRepository registrationRepository;
    private CallbackEndpointController callbackEndpointController;
    private DidAuthFlow didAuthFlow;

    @Before
    public void setUp() {
        this.didTokenRepository = new InMemoryDidTokenRepository(60);
        this.registrationRepository = new InMemoryRegistrationRepository(60);
        this.didAuthFlow = mock(DidAuthFlow.class);
        this.callbackEndpointController = new CallbackEndpointController(didAuthFlow, didTokenRepository, registrationRepository);
    }

    @Test
    public void callbackEndpointShouldUpdateToken() {
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
