package com.sphereon.cas.did.auth.passwordless.web.flow;

import com.sphereon.cas.did.auth.passwordless.config.DidAuthConstants;
import com.sphereon.cas.did.auth.passwordless.repository.RegistrationRepository;
import com.sphereon.cas.did.auth.passwordless.repository.model.RegistrationRequest;
import com.sphereon.libs.did.auth.client.DidAuthFlow;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class CreateDidRegistrationRequestAction extends AbstractAction {
    private final RegistrationRepository registrationRepository;
    private final DidAuthFlow didAuthFlow;
    private final String appId;
    private final String baseCasUrl;

    public CreateDidRegistrationRequestAction(RegistrationRepository registrationRepository, DidAuthFlow didAuthFlow, String appId, String baseCasUrl) {
        this.registrationRepository = registrationRepository;
        this.didAuthFlow = didAuthFlow;
        this.appId = appId;
        this.baseCasUrl = baseCasUrl;
    }

    @Override
    public Event doExecute(final RequestContext requestContext) {
        String username = requestContext.getRequestParameters().get("username");
        String callbackUrl = baseCasUrl + DidAuthConstants.Endpoints.TokenCallback.REGISTER;
        try {
            final var registrationId = generateRegistrationId();
            final var registrationRequest = new RegistrationRequest()
                    .appId(appId)
                    .userName(username)
                    .registrationId(registrationId)
                    .qrCodeBase64(didAuthFlow.dispatchRegistrationRequest(registrationId, callbackUrl));
            registrationRepository.saveRegistrationRequest(registrationId, registrationRequest);

            requestContext.getFlowScope().put("didRegistrationQR", registrationRequest.getQrCodeBase64());
            return success();
        } catch (Exception e) {
            LOGGER.error(String.format("DID registration request failed for app id %s. The callback url is %s",
                    appId, callbackUrl), e);
            return error();
        }
    }


    private String generateRegistrationId() {
        var random = ThreadLocalRandom.current();
        var buffer = new byte[64];
        random.nextBytes(buffer);
        return Base64.encodeBase64String(buffer);
    }
}
