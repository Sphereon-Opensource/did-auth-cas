package com.sphereon.cas.did.auth.passwordless.callback;

import com.sphereon.cas.did.auth.passwordless.callback.model.CallbackRegistrationRequest;
import com.sphereon.cas.did.auth.passwordless.callback.model.CallbackTokenPostRequest;
import com.sphereon.cas.did.auth.passwordless.config.DidAuthConstants;
import com.sphereon.cas.did.auth.passwordless.repository.RegistrationRepository;
import com.sphereon.cas.did.auth.passwordless.repository.model.DidToken;
import com.sphereon.cas.did.auth.passwordless.repository.DidTokenRepository;
import com.sphereon.cas.did.auth.passwordless.repository.model.RegistrationRequest;
import com.sphereon.libs.did.auth.client.DidAuthFlow;
import com.sphereon.libs.did.auth.client.KUtilsKt;
import com.sphereon.libs.did.auth.client.model.UserInfo;
import kotlin.Triple;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@RestController
public class CallbackEndpointController {

    private final DidAuthFlow didAuthFlow;

    private final DidTokenRepository didTokenRepository;

    private final RegistrationRepository registrationRepository;

    public CallbackEndpointController(final DidAuthFlow didAuthFlow, final DidTokenRepository didTokenRepository,
                                      final RegistrationRepository registrationRepository) {
        this.didAuthFlow = didAuthFlow;
        this.didTokenRepository = didTokenRepository;
        this.registrationRepository = registrationRepository;
    }

    @PostMapping(value = DidAuthConstants.Endpoints.TokenCallback.POST_LOGIN_TOKEN,
            consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE},
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public ResponseEntity postLoginToken(@PathVariable(value = DidAuthConstants.Param.USERNAME) String username,
                                         @RequestBody CallbackTokenPostRequest request) {
        /* TODO: 05-12-19 Here we need to check the validity of the received JWT and if invalid, interrupt the
            current login session. This will be possible upon implementing https://sphereon.atlassian.net/browse/SPHEREON-824
        */
        final DidToken currentToken = didTokenRepository.findToken(username)
                .filter(token -> token.getRequestToken() != null)
                .orElseThrow(() -> {
                    LOGGER.error("Callback endpoint called, but no token found for " + username);
                    return new HttpClientErrorException(HttpStatus.BAD_REQUEST);
                });
        String responseJwt = request.getAccess_token();
        DidToken newDidToken = currentToken.with(responseJwt);
        didTokenRepository.saveToken(username, newDidToken);
        return ResponseEntity.ok().build();
    }


    @PostMapping(value = DidAuthConstants.Endpoints.TokenCallback.REGISTER,
            consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE},
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public ResponseEntity registerDid(@RequestBody CallbackRegistrationRequest request) {

        final String requestJwt = request.getAccess_token();
        final String registrationId = didAuthFlow.registrationRequestIdFromToken(requestJwt);
        final var registrationRequest = registrationRepository.findRegistrationRequest(registrationId)
                .orElseThrow(() -> {
                    LOGGER.error(String.format("Callback endpoint called, but registrationId %s found for.", registrationId));
                    return new HttpClientErrorException(HttpStatus.BAD_REQUEST);
                });

        final var userInfo = new UserInfo(request.getDid(), request.getBoxPub(), request.getPushToken());
        didAuthFlow.registerDidMapping(registrationRequest.getAppId(), registrationRequest.getUserName(), userInfo);
        return ResponseEntity.ok().build();
    }
}
