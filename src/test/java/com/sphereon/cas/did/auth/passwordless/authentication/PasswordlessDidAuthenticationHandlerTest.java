package com.sphereon.cas.did.auth.passwordless.authentication;

import com.sphereon.cas.did.auth.passwordless.repository.model.DidToken;
import com.sphereon.cas.did.auth.passwordless.repository.InMemoryDidTokenRepository;
import com.sphereon.libs.did.auth.client.DidAuthFlow;
import com.sphereon.libs.did.auth.client.exceptions.MalformedLoginJwtException;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.credential.OneTimePasswordCredential;
import org.apereo.cas.authentication.principal.PrincipalFactoryUtils;
import org.apereo.cas.services.ServicesManager;
import org.junit.Before;
import org.junit.Test;

import javax.security.auth.login.FailedLoginException;
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
    public void authenticateShouldVerifyWhenDidAuthFlowVerifies() throws GeneralSecurityException, PreventedException {
        var repository = new InMemoryDidTokenRepository(60);
        var token = new DidToken("testRequestToken");
        repository.saveToken("testUser", token.with("testResponseToken"));
        var c = new OneTimePasswordCredential("testUser", "testResponseToken");
        when(didAuthFlow.verifyLoginToken("testResponseToken")).thenReturn("testResponseToken");
        assertNotNull(passwordlessDidAuthenticationHandler.authenticate(c));
    }

    @Test(expected = FailedLoginException.class)
    public void authenticateShouldNotVerifyWhenDidAuthThrowsError() throws GeneralSecurityException, PreventedException {
        var repository = new InMemoryDidTokenRepository(60);
        var token = new DidToken("testRequestToken");
        repository.saveToken("testUser", token.with("testResponseToken"));
        var c = new OneTimePasswordCredential("testUser", "testResponseToken");
        when(didAuthFlow.verifyLoginToken("testResponseToken"))
                .thenThrow(new MalformedLoginJwtException("JWT does not contain correct signature"));
        passwordlessDidAuthenticationHandler.authenticate(c);
    }
}
