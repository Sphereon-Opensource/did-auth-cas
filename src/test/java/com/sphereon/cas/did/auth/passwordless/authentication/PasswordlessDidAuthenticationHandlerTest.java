package com.sphereon.cas.did.auth.passwordless.authentication;

import com.sphereon.cas.did.auth.passwordless.token.DidToken;
import com.sphereon.cas.did.auth.passwordless.token.InMemoryDidTokenRepository;
import com.sphereon.libs.did.auth.client.DidAuthFlow;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.credential.OneTimePasswordCredential;
import org.apereo.cas.authentication.principal.PrincipalFactoryUtils;
import org.apereo.cas.services.ServicesManager;
import org.junit.Before;
import org.junit.Test;

import java.security.GeneralSecurityException;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PasswordlessDidAuthenticationHandlerTest {
    private DidAuthFlow didAuthFlow;
    private PasswordlessDidAuthenticationHandler passwordlessDidAuthenticationHandler;

    @Before
    public void setUp() {
        this.didAuthFlow = mock(DidAuthFlow.class);
        this.passwordlessDidAuthenticationHandler = new PasswordlessDidAuthenticationHandler(null,
                mock(ServicesManager.class),
                PrincipalFactoryUtils.newPrincipalFactory(),
                0,
                this.didAuthFlow);
    }

    @Test
    public void verifyAction() throws GeneralSecurityException, PreventedException {
        var repository = new InMemoryDidTokenRepository(60);
        var token = new DidToken("testRequestToken");
        repository.saveToken("testUser", token.with("testResponseToken"));
        var c = new OneTimePasswordCredential("testUser", "responseToken");
        when(didAuthFlow.verifyLoginToken("testResponseToken")).thenReturn("testResponseToken");
        assertNotNull(passwordlessDidAuthenticationHandler.authenticate(c));
    }
}
