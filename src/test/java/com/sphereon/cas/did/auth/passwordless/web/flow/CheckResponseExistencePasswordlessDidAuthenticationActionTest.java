package com.sphereon.cas.did.auth.passwordless.web.flow;

import com.sphereon.cas.did.auth.passwordless.repository.model.DidToken;
import com.sphereon.cas.did.auth.passwordless.repository.DidTokenRepository;
import com.sphereon.cas.did.auth.passwordless.repository.InMemoryDidTokenRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.webflow.core.collection.ParameterMap;
import org.springframework.webflow.execution.Action;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CheckResponseExistencePasswordlessDidAuthenticationActionTest {
    private DidTokenRepository didTokenRepository;
    private Action checkResponseExistenceAction;
    private RequestContext requestContext;

    @Before
    public void setUp() {
        this.didTokenRepository = new InMemoryDidTokenRepository(60);
        this.checkResponseExistenceAction = new CheckResponseExistencePasswordlessDidAuthenticationAction(didTokenRepository);
        this.requestContext = mock(RequestContext.class);
        var requestParameters = mock(ParameterMap.class);

        when(requestParameters.get("username")).thenReturn("testUsername");
        when(requestContext.getRequestParameters()).thenReturn(requestParameters);
    }

    @Test
    public void checkResponseExistenceActionShouldFailWhenResponseIsNotPresent() throws Exception {
        didTokenRepository.saveToken("testUsername", new DidToken("testRequest"));
        Event result = checkResponseExistenceAction.execute(requestContext);
        assertEquals(result.getId(), "error");
    }

    @Test
    public void checkResponseExistenceActionShouldSucceedWhenResponseIsPresent() throws Exception {
        var didToken = new DidToken("request");
        var didTokenUpdated = didToken.with("response");
        didTokenRepository.saveToken("testUsername", didTokenUpdated);
        Event result = checkResponseExistenceAction.execute(requestContext);
        assertEquals(result.getId(), "success");
    }
}
