package com.sphereon.cas.did.auth.passwordless.web.flow;

import com.sphereon.cas.did.auth.passwordless.repository.model.DidToken;
import com.sphereon.cas.did.auth.passwordless.repository.DidTokenRepository;
import com.sphereon.cas.did.auth.passwordless.repository.InMemoryDidTokenRepository;
import org.apereo.cas.authentication.AuthenticationSystemSupport;
import org.apereo.cas.authentication.adaptive.AdaptiveAuthenticationPolicy;
import org.apereo.cas.web.flow.CasWebflowConstants;
import org.apereo.cas.web.flow.resolver.CasDelegatingWebflowEventResolver;
import org.apereo.cas.web.flow.resolver.CasWebflowEventResolver;
import org.junit.Before;
import org.junit.Test;
import org.springframework.webflow.core.collection.ParameterMap;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AcceptPasswordlessDidAuthenticationActionTest {
    private DidTokenRepository didTokenRepository;
    private Action acceptAuthenticationAction;
    private RequestContext requestContext;
    private String testUsername = "testUsername";

    @Before
    public void setUp() {
        this.didTokenRepository = new InMemoryDidTokenRepository(60);
        var delegatingWebflowEventResolver = mock(CasDelegatingWebflowEventResolver.class);
        var webflowEventResolver = mock(CasWebflowEventResolver.class);
        var authPolicy = mock(AdaptiveAuthenticationPolicy.class);
        var authSupport = mock(AuthenticationSystemSupport.class);

        this.acceptAuthenticationAction = new AcceptPasswordlessDidAuthenticationAction(
                delegatingWebflowEventResolver,
                webflowEventResolver,
                authPolicy,
                didTokenRepository,
                authSupport);

        this.requestContext = mock(RequestContext.class);
        var requestParameters = mock(ParameterMap.class);

        when(requestParameters.get("username")).thenReturn(testUsername);
        when(requestContext.getRequestParameters()).thenReturn(requestParameters);
    }

    @Test
    public void acceptAuthenticationActionShouldFailWhenTokenResponseIsNull() throws Exception {
        var didToken = new DidToken("request");
        var nullResponseDidToken = didToken.with(null);
        didTokenRepository.saveToken(testUsername, nullResponseDidToken);
        Event result = acceptAuthenticationAction.execute(requestContext);
        assertEquals(result.getId(), CasWebflowConstants.TRANSITION_ID_AUTHENTICATION_FAILURE);
    }
}
