package com.sphereon.cas.did.auth.passwordless.callback;

import com.sphereon.cas.did.auth.passwordless.config.DidAuthConstants;
import com.sphereon.cas.did.auth.passwordless.token.DidToken;
import com.sphereon.cas.did.auth.passwordless.token.DidTokenRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@RestController
public class CallbackEndpointController {

    private final DidTokenRepository didTokenRepository;

    public CallbackEndpointController(final DidTokenRepository didTokenRepository) {
        this.didTokenRepository = didTokenRepository;
    }

    //endpoint can be found at https://localhost:8443/cas/test
    @GetMapping(value = "/test",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public String generate(final HttpServletResponse response, final HttpServletRequest request) {
        System.out.println(response);
        System.out.println(request);
        return "IT WORKS!";
    }

    @PostMapping(value = DidAuthConstants.Endpoints.TokenCallback.POST_LOGIN_TOKEN,
            consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE},
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public ResponseEntity postLoginToken(@PathVariable(value = DidAuthConstants.Param.USERNAME) String username,
                                         String responseJwt) {
        Optional<DidToken> currentToken = didTokenRepository.findToken(username);
        if (currentToken.isEmpty() || currentToken.get().getRequestToken() == null) {
            return ResponseEntity.badRequest().build();
        }
        String request = currentToken.get().getRequestToken();
        didTokenRepository.updateToken(username, request, responseJwt);
        return ResponseEntity.ok().build();
    }
}
