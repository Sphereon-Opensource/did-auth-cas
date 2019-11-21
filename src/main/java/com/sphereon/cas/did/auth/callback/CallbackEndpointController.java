package com.sphereon.cas.did.auth.callback;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class CallbackEndpointController {

    public CallbackEndpointController(){}

    //endpoint can be found at https://localhost:8443/cas/test
    @GetMapping(value = "/test",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public String generate(final HttpServletResponse response, final HttpServletRequest request){
        System.out.println(response);
        System.out.println(request);
        return "IT WORKS!";
    }
}
