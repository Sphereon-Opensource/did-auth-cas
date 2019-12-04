package com.sphereon.cas.did.auth.passwordless.callback;

import com.sphereon.cas.did.auth.passwordless.callback.model.CallbackTokenPostRequest;
import com.sphereon.cas.did.auth.passwordless.config.DidAuthConstants;
import com.sphereon.cas.did.auth.passwordless.token.DidToken;
import com.sphereon.cas.did.auth.passwordless.token.DidTokenRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

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

        String responseJwt = request.getAccess_token();
        Optional<DidToken> currentToken = didTokenRepository.findToken(username);
        if (currentToken.isEmpty() || currentToken.get().getRequestToken() == null) {
            return ResponseEntity.badRequest().build();
        }
        DidToken newDidToken = currentToken.get().with(responseJwt);
        didTokenRepository.saveToken(username, newDidToken);
        return ResponseEntity.ok().build();
    }
}
