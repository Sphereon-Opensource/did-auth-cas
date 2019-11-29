package com.sphereon.cas.did.auth.passwordless.web.flow;

import com.sphereon.cas.did.auth.passwordless.config.DidAuthConstants;
import com.sphereon.cas.did.auth.passwordless.token.DidToken;
import com.sphereon.cas.did.auth.passwordless.token.DidTokenRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import java.util.Optional;

public class CheckResponseExistencePasswordlessDidAuthenticationAction extends AbstractAction {
    private final DidTokenRepository didTokenRepository;

    public CheckResponseExistencePasswordlessDidAuthenticationAction(DidTokenRepository didTokenRepository) {
        this.didTokenRepository = didTokenRepository;
    }

    @Override
    public Event doExecute(final RequestContext requestContext) throws Exception {
        System.out.println("CheckResponseExistencePasswordlessDidAuthenticaitonAction - Checking for response existence");
        String username = requestContext.getRequestParameters().get("username");

        if (StringUtils.isBlank(username)) {
            System.out.println("Username empty.");
            return error();
        }

        Optional<DidToken> userToken = didTokenRepository.findToken(username);
        if (userToken.isEmpty()) {
            System.out.println("User token not found for username: " + username);
            return error();
        }

        DidToken token = userToken.get();

        if (token.getResponseToken() != null && token.getResponseToken().equals(DidAuthConstants.Token.NOT_INITIALIZED)) {
            return error();
        }

        return success();
    }
}
