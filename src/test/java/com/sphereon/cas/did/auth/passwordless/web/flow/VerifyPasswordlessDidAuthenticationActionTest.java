package com.sphereon.cas.did.auth.passwordless.web.flow;

import com.sphereon.cas.did.auth.passwordless.repository.DidToken;
import com.sphereon.cas.did.auth.passwordless.repository.DidTokenRepository;
import com.sphereon.cas.did.auth.passwordless.repository.InMemoryDidTokenRepository;
import com.sphereon.libs.did.auth.client.DidAuthFlow;
import com.sphereon.libs.did.auth.client.exceptions.UserNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.webflow.core.collection.ParameterMap;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import java.util.Optional;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VerifyPasswordlessDidAuthenticationActionTest {
    private DidTokenRepository didTokenRepository;
    private DidAuthFlow didAuthFlow;
    private Action verifyAuthenticationAction;
    private RequestContext requestContext;
    private String appId = "testAppId";
    private String baseCasUrl = "/test/cas/url";
    private String testUsername = "testUsername";
    private String callback = baseCasUrl + "/login/" + testUsername;

    @Before
    public void setUp() {
        this.didTokenRepository = new InMemoryDidTokenRepository(60);
        this.didAuthFlow = mock(DidAuthFlow.class);
        this.verifyAuthenticationAction = new VerifyPasswordlessDidAuthenticationAction(
                didTokenRepository,
                didAuthFlow,
                appId,
                baseCasUrl);
        this.requestContext = mock(RequestContext.class);
        var requestParameters = mock(ParameterMap.class);

        when(requestParameters.get("username")).thenReturn(testUsername);
        when(requestContext.getRequestParameters()).thenReturn(requestParameters);
    }

    @Test
    public void verifyAuthenticationActionShouldPopulateRequestTokenInRepository() throws Exception {
        when(didAuthFlow.dispatchLoginRequest(appId, testUsername, callback)).thenReturn("requestToken");
        verifyAuthenticationAction.execute(requestContext);
        Optional<DidToken> didTokenOptional = didTokenRepository.findToken(testUsername);
        assertTrue(didTokenOptional.isPresent());
        assertFalse(didTokenOptional.get().isResponseReceived());
        assertEquals(didTokenOptional.get().getRequestToken(), "requestToken");
    }

    @Test
    public void verifyAuthenticationActionShouldReturnSuccessWhenDidAuthFlowIsSuccessful() throws Exception {
        when(didAuthFlow.dispatchLoginRequest(appId, testUsername, callback)).thenReturn("requestToken");
        Event result = verifyAuthenticationAction.execute(requestContext);
        assertEquals(result.getId(), "success");
    }

    @Test
    public void verifyAuthenticationActionShouldFailWhenDidAuthFlowFails() throws Exception {
        when(didAuthFlow.dispatchLoginRequest(appId, testUsername, callback))
                .thenThrow(
                        new UserNotFoundException("didInfo for user " + testUsername + " is missing necessary information.")
                );
        Event result = verifyAuthenticationAction.execute(requestContext);
        assertEquals(result.getId(), "error");
    }
}
