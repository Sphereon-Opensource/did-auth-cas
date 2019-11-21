package com.sphereon.cas.did.auth.passwordless.web.flow;

import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.web.flow.login.InitializeLoginAction;
import org.apereo.cas.web.support.WebUtils;
import org.springframework.webflow.action.EventFactorySupport;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

public class PrepareForPasswordlessDidAuthentication extends InitializeLoginAction {

    public PrepareForPasswordlessDidAuthentication(ServicesManager servicesManager) {
        super(servicesManager);
    }

    @Override
    public Event doExecute(final RequestContext requestContext) throws Exception {
        WebUtils.putPasswordlessAuthenticationEnabled(requestContext, Boolean.TRUE);
        if (!WebUtils.hasPasswordlessAuthenticationAccount(requestContext)) {
            return new EventFactorySupport().event(this, PasswordlessDidAuthenticationWebflowConfigurer.TRANSITION_ID_PASSWORDLESS_GET_USERID);
        }
        return super.doExecute(requestContext);
    }
}
