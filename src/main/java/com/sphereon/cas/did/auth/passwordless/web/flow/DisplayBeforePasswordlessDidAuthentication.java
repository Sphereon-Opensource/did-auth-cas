package com.sphereon.cas.did.auth.passwordless.web.flow;


import com.sphereon.cas.did.auth.passwordless.api.PasswordlessUserAccount;
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
        String username = requestContext.getRequestParameters().get("username");
        PasswordlessUserAccount user = new PasswordlessUserAccount(username);
        WebUtils.putPasswordlessAuthenticationAccount(requestContext, user);

        if (StringUtils.isBlank(username)) {
            throw new UnauthorizedServiceException(UnauthorizedServiceException.CODE_UNAUTHZ_SERVICE, StringUtils.EMPTY);
        }
        return success();
    }
}
