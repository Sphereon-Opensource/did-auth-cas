package com.sphereon.cas.did.auth.passwordless.web.flow;

import com.sphereon.libs.did.auth.client.DidMappingService;
import com.sphereon.libs.did.auth.client.exceptions.UserNotFoundException;
import com.sphereon.libs.did.auth.client.model.UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.message.MessageResolver;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

public class VerifyPasswordlessDidAuthenticationAction extends AbstractAction {
    private final DidMappingService didMappingService;
    private final String appId;

    public VerifyPasswordlessDidAuthenticationAction(final DidMappingService didMappingService, String appId) {
        this.didMappingService = didMappingService;
        this.appId = appId;
    }

    @Override
    public Event doExecute(final RequestContext requestContext) throws Exception {
        MessageContext messageContext = requestContext.getMessageContext();
        String username = requestContext.getRequestParameters().get("username");

        if (StringUtils.isBlank(username)) {
            MessageResolver message = new MessageBuilder().error().code("passwordless.error.unknown.user").build();
            messageContext.addMessage(message);
            return error();
        }
        try {
            UserInfo userInfo = didMappingService.getUserInfo(appId, username);

            if (StringUtils.isBlank(userInfo.getBoxPub()) || StringUtils.isBlank(userInfo.getDid()) || StringUtils.isBlank(userInfo.getPushToken())) {
                MessageResolver message = new MessageBuilder().error().code("passwordless.error.invalid.user").build();
                messageContext.addMessage(message);
                return error();
            }
        } catch (UserNotFoundException e) {
            MessageResolver message = new MessageBuilder().error().code("passwordless.error.unknown.user").build();
            messageContext.addMessage(message);
            return error();
        }
        return success();
    }
}
