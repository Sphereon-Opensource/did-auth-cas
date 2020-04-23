package com.sphereon.cas.did.auth.passwordless.callback;

import com.sphereon.cas.did.auth.passwordless.callback.model.CallbackTokenPostRequest;
import com.sphereon.cas.did.auth.passwordless.config.DidAuthConstants;
import com.sphereon.cas.did.auth.passwordless.repository.DidToken;
import com.sphereon.cas.did.auth.passwordless.repository.DidTokenRepository;
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

    private final DidTokenRepository didTokenRepository;

    public CallbackEndpointController(final DidTokenRepository didTokenRepository) {
        this.didTokenRepository = didTokenRepository;
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

    // TODO: Add handler for DidAuthConstants.Endpoints.TokenCallback.REGISTER
}
