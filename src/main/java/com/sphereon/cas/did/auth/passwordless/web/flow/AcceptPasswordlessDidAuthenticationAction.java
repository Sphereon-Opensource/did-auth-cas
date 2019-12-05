package com.sphereon.cas.did.auth.passwordless.web.flow;

import com.sphereon.cas.did.auth.passwordless.token.DidToken;
import com.sphereon.cas.did.auth.passwordless.token.DidTokenRepository;
import org.apereo.cas.authentication.AuthenticationException;
import org.apereo.cas.authentication.AuthenticationResult;
import org.apereo.cas.authentication.AuthenticationSystemSupport;
import org.apereo.cas.authentication.adaptive.AdaptiveAuthenticationPolicy;
import org.apereo.cas.authentication.credential.OneTimePasswordCredential;
import org.apereo.cas.authentication.principal.WebApplicationService;
import org.apereo.cas.web.flow.CasWebflowConstants;
import org.apereo.cas.web.flow.actions.AbstractAuthenticationAction;
import org.apereo.cas.web.flow.resolver.CasDelegatingWebflowEventResolver;
import org.apereo.cas.web.flow.resolver.CasWebflowEventResolver;
import org.apereo.cas.web.support.WebUtils;
import org.springframework.webflow.action.EventFactorySupport;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import java.util.Optional;

public class AcceptPasswordlessDidAuthenticationAction extends AbstractAuthenticationAction {
    private final DidTokenRepository didTokenRepository;
    private final AuthenticationSystemSupport authenticationSystemSupport;

    public AcceptPasswordlessDidAuthenticationAction(final CasDelegatingWebflowEventResolver initialAuthenticationAttemptWebflowEventResolver,
                                                     final CasWebflowEventResolver serviceTicketRequestWebflowEventResolver,
                                                     final AdaptiveAuthenticationPolicy adaptiveAuthenticationPolicy,
                                                     final DidTokenRepository didTokenRepository,
                                                     final AuthenticationSystemSupport authenticationSystemSupport) {
        super(initialAuthenticationAttemptWebflowEventResolver, serviceTicketRequestWebflowEventResolver, adaptiveAuthenticationPolicy);
        this.didTokenRepository = didTokenRepository;
        this.authenticationSystemSupport = authenticationSystemSupport;
    }

    @Override
    protected Event doExecute(final RequestContext requestContext) {
        String username = requestContext.getRequestParameters().get("username");
        try {
            Optional<DidToken> currentToken = didTokenRepository.findToken(username);
            if (currentToken.isEmpty() || currentToken.get().getResponseToken() == null) {
                LocalAttributeMap<Object> attributes = new LocalAttributeMap<>();
                attributes.put("error", new AuthenticationException("Invalid token"));
                return new EventFactorySupport().event(this, CasWebflowConstants.TRANSITION_ID_AUTHENTICATION_FAILURE, attributes);
            }
            String password = currentToken.get().getResponseToken();
            OneTimePasswordCredential credential = new OneTimePasswordCredential(username, password);
            WebApplicationService service = WebUtils.getService(requestContext);
            AuthenticationResult authenticationResult = authenticationSystemSupport.handleAndFinalizeSingleAuthenticationTransaction(service, credential);
            WebUtils.putAuthenticationResult(authenticationResult, requestContext);
            WebUtils.putAuthentication(authenticationResult.getAuthentication(), requestContext);
            WebUtils.putCredential(requestContext, credential);
            Event finalEvent = super.doExecute(requestContext);
            didTokenRepository.deleteToken(username);
            return finalEvent;
        } catch (final Exception e) {
            LocalAttributeMap<Object> attributes = new LocalAttributeMap<>();
            attributes.put("error", e);
            attributes.put("passwordlessAccount", username);
            return new EventFactorySupport().event(this, CasWebflowConstants.TRANSITION_ID_AUTHENTICATION_FAILURE, attributes);
        }
    }
}
