package com.sphereon.cas.did.auth.simpleexample;

import org.apereo.cas.authentication.AuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.credential.UsernamePasswordCredential;
import org.apereo.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import javax.security.auth.login.FailedLoginException;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.services.ServicesManager;

public class ExampleAuthenticationHandler extends AbstractUsernamePasswordAuthenticationHandler {

    public ExampleAuthenticationHandler(String name, ServicesManager servicesManager, PrincipalFactory principalFactory, Integer order) {
        super(name, servicesManager, principalFactory, order);
    }

    @Override
    protected AuthenticationHandlerExecutionResult authenticateUsernamePasswordInternal(final UsernamePasswordCredential credential, final String originalPassword) throws FailedLoginException {
        String username = credential.getUsername();
        System.out.println("Using custom authentication handler");
        if (username.equals("Scott")) {
            return createHandlerResult(credential, this.principalFactory.createPrincipal(username));
        }
        throw new FailedLoginException();
    }
}
