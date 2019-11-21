package com.sphereon.cas.did.auth.passwordless.web.flow;


import com.sphereon.cas.did.auth.passwordless.token.DidToken;
import com.sphereon.cas.did.auth.passwordless.token.DidTokenRepository;
import com.sphereon.libs.did.auth.client.DidAuthFlow;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.services.UnauthorizedServiceException;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

public class DisplayBeforePasswordlessDidAuthentication extends AbstractAction {
    private final DidAuthFlow didAuthFlow;
    private final DidTokenRepository didTokenRepository;
    private final String appId;

    private final String callbackUrl = "https://localhost:8443/cas/test";

    public DisplayBeforePasswordlessDidAuthentication(DidAuthFlow didAuthFlow,
                                                      DidTokenRepository didTokenRepository,
                                                      final String appId) {
        this.didAuthFlow = didAuthFlow;
        this.didTokenRepository = didTokenRepository;
        this.appId = appId;
    }

    @Override
    protected Event doExecute(final RequestContext requestContext) {
        String username = requestContext.getRequestParameters().get("username");
        if (StringUtils.isBlank(username)) {
            throw new UnauthorizedServiceException(UnauthorizedServiceException.CODE_UNAUTHZ_SERVICE, StringUtils.EMPTY);
        }
        try {
            String requestJwt = didAuthFlow.dispatchLoginRequest(appId, username, callbackUrl);
            DidToken token = didTokenRepository.createToken(username, requestJwt);
            didTokenRepository.deleteToken(username);
            didTokenRepository.saveToken(username, token);
            return success();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return error();
        }
    }
}
