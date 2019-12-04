package com.sphereon.cas.did.auth.passwordless.callback.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class CallbackTokenPostRequest{
    private final String access_token;

    @JsonCreator
    public CallbackTokenPostRequest(@JsonProperty("access_token") final String access_token){
        this.access_token = access_token;
    }
}
