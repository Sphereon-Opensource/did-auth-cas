package com.sphereon.cas.did.auth.passwordless.web.flow;

import com.sphereon.cas.did.auth.passwordless.config.DidAuthConstants;
import com.sphereon.cas.did.auth.passwordless.token.DidToken;
import com.sphereon.cas.did.auth.passwordless.token.DidTokenRepository;
import com.sphereon.libs.did.auth.client.DidAuthFlow;
import com.sphereon.libs.did.auth.client.exceptions.UserNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.message.MessageResolver;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

public class VerifyPasswordlessDidAuthenticationAction extends AbstractAction {
    private final DidTokenRepository didTokenRepository;
    private final DidAuthFlow didAuthFlow;
    private final String appId;
    private final String baseCasUrl;

    public VerifyPasswordlessDidAuthenticationAction(DidTokenRepository didTokenRepository, DidAuthFlow didAuthFlow, String appId, String baseCasUrl) {
        this.didTokenRepository = didTokenRepository;
        this.didAuthFlow = didAuthFlow;
        this.appId = appId;
        this.baseCasUrl = baseCasUrl;
    }

    @Override
    public Event doExecute(final RequestContext requestContext) {
        System.out.println("VerifyPasswordlessDidAuthentication - Dispatching Login Token");
        MessageContext messageContext = requestContext.getMessageContext();

        String username = requestContext.getRequestParameters().get("username");
        System.out.println("Username: " + username);

        String callbackUrl = baseCasUrl + DidAuthConstants.Endpoints.TokenCallback.NAME + "/" + username;
        System.out.println("Callback: "+callbackUrl);

        if (StringUtils.isBlank(username)) {
            MessageResolver message = new MessageBuilder().error().code("passwordless.error.unknown.user").build();
            messageContext.addMessage(message);
            return error();
        }

        try {
            System.out.println("Logging in " + username + " with appId: " + appId);
            String requestJwt = didAuthFlow.dispatchLoginRequest(appId, username, callbackUrl);
            System.out.println("Request JWT: " + requestJwt);
            DidToken token = didTokenRepository.createToken(username, requestJwt);
            didTokenRepository.deleteToken(username);
            didTokenRepository.saveToken(username, token);
            return success();
        } catch (UserNotFoundException e) {
            MessageResolver message = new MessageBuilder().error().code("passwordless.error.invalid.user").build();
            messageContext.addMessage(message);
            return error();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return error();
        }
    }
}
