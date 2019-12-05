package com.sphereon.cas.did.auth.passwordless.web.flow;

import com.sphereon.cas.did.auth.passwordless.token.DidToken;
import com.sphereon.cas.did.auth.passwordless.token.DidTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.web.flow.CasWebflowConstants;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import java.util.Optional;

@Slf4j
public class CheckResponseExistencePasswordlessDidAuthenticationAction extends AbstractAction {
    private final DidTokenRepository didTokenRepository;

    public CheckResponseExistencePasswordlessDidAuthenticationAction(DidTokenRepository didTokenRepository) {
        this.didTokenRepository = didTokenRepository;
    }

    @Override
    public Event doExecute(final RequestContext requestContext) {
        String username = requestContext.getRequestParameters().get("username");

        if (StringUtils.isBlank(username)) {
            LOGGER.error("Username empty.");
            return error();
        }

        Optional<DidToken> userToken = didTokenRepository.findToken(username);
        if (userToken.isEmpty()) {
            LOGGER.error("User token not found for username: " + username);
            return getEventFactorySupport().event(this, CasWebflowConstants.TRANSITION_ID_CANCEL);
        }

        DidToken token = userToken.get();
        return token.isResponseReceived() ? success() : error();
    }
}
