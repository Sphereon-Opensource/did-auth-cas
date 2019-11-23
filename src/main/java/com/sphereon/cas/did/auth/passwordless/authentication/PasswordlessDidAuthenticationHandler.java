package com.sphereon.cas.did.auth.passwordless.authentication;

import com.sphereon.libs.did.auth.client.DidAuthFlow;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.authentication.AuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.credential.OneTimePasswordCredential;
import org.apereo.cas.authentication.handler.support.AbstractPreAndPostProcessingAuthenticationHandler;
import org.apereo.cas.authentication.principal.Principal;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.services.ServicesManager;

import javax.security.auth.login.FailedLoginException;
import java.security.GeneralSecurityException;

public class PasswordlessDidAuthenticationHandler extends AbstractPreAndPostProcessingAuthenticationHandler {
    private final DidAuthFlow didAuthFlow;

    public PasswordlessDidAuthenticationHandler(final String name, final ServicesManager servicesManager,
                                                final PrincipalFactory principalFactory, final Integer order,
                                                final DidAuthFlow didAuthFlow) {
        super(name, servicesManager, principalFactory, order);
        this.didAuthFlow = didAuthFlow;
    }

    @Override
    protected AuthenticationHandlerExecutionResult doAuthentication(final Credential credential) throws GeneralSecurityException {
        OneTimePasswordCredential c = (OneTimePasswordCredential) credential;
        String username = c.getId();
        String responseJwt = c.getPassword();
        if (StringUtils.isEmpty(responseJwt)) {
            throw new FailedLoginException("Passwordless DID authentication failed. No token found");
        }
        try {
            didAuthFlow.verifyLoginToken(responseJwt);
        } catch (Exception e) {
            throw new FailedLoginException("Passwordless authentication failed");
        }
        Principal principal = principalFactory.createPrincipal(username);
        return createHandlerResult(credential, principal);


    }

    @Override
    public boolean supports(final Class<? extends Credential> clazz) {
        return OneTimePasswordCredential.class.isAssignableFrom(clazz);
    }

    @Override
    public boolean supports(final Credential credential) {
        if (!(credential instanceof OneTimePasswordCredential)) {
            System.out.println("Credential is not one of one-time password and is not accepted by handler [{}]" + getName());
            return false;
        }
        return true;
    }
}
