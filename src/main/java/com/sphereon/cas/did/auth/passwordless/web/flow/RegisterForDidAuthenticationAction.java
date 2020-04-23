package com.sphereon.cas.did.auth.passwordless.web.flow;

import com.sphereon.cas.did.auth.passwordless.config.DidAuthConstants;
import com.sphereon.cas.did.auth.passwordless.repository.DidTokenRepository;
import com.sphereon.cas.did.auth.passwordless.repository.RegistrationQRCodeRepository;
import com.sphereon.libs.did.auth.client.DidAuthFlow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import java.util.UUID;

@Slf4j
public class RegisterForDidAuthenticationAction extends AbstractAction {
    private final RegistrationQRCodeRepository registrationQRCodeRepository;
    private final DidAuthFlow didAuthFlow;
    private final String appId;
    private final String baseCasUrl;

    public RegisterForDidAuthenticationAction(RegistrationQRCodeRepository registrationQRCodeRepository, DidAuthFlow didAuthFlow, String appId, String baseCasUrl) {
        this.registrationQRCodeRepository = registrationQRCodeRepository;
        this.didAuthFlow = didAuthFlow;
        this.appId = appId;
        this.baseCasUrl = baseCasUrl;
    }

    @Override
    public Event doExecute(final RequestContext requestContext) {
        String topic = UUID.randomUUID().toString();
        String callbackUrl = baseCasUrl + DidAuthConstants.Endpoints.TokenCallback.REGISTER + "?topic=" + topic;
        try {
            String qrCodeBase64 = didAuthFlow.dispatchRegistrationRequest(appId, callbackUrl);
            registrationQRCodeRepository.saveQR(topic, qrCodeBase64);
            return success();
        } catch (Exception e) {
            LOGGER.error(String.format("DID registration request %s failed for app id %s. The callback url is %s",
                    topic, appId, callbackUrl), e);
            return error();
        }
    }
}
