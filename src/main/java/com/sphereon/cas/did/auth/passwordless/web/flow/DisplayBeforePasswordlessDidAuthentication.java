package com.sphereon.cas.did.auth.passwordless.web.flow;


import com.sphereon.cas.did.auth.passwordless.api.PasswordlessUserAccount;
import com.sphereon.cas.did.auth.passwordless.config.DidAuthConstants;
import com.sphereon.cas.did.auth.passwordless.token.DidToken;
import com.sphereon.cas.did.auth.passwordless.token.DidTokenRepository;
import com.sphereon.libs.did.auth.client.DidAuthFlow;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.services.UnauthorizedServiceException;
import org.apereo.cas.web.support.WebUtils;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

public class DisplayBeforePasswordlessDidAuthentication extends AbstractAction {

    public DisplayBeforePasswordlessDidAuthentication(){
    }

    @Override
    protected Event doExecute(final RequestContext requestContext) {
        System.out.println("DisplayBeforePasswordlessDidAuthentication");
        String username = requestContext.getRequestParameters().get("username");
        PasswordlessUserAccount user = new PasswordlessUserAccount(username, "email", "phone", "name");
        WebUtils.putPasswordlessAuthenticationAccount(requestContext, user);




        if (StringUtils.isBlank(username)) {
            throw new UnauthorizedServiceException(UnauthorizedServiceException.CODE_UNAUTHZ_SERVICE, StringUtils.EMPTY);
        }
        return success();
    }
}
