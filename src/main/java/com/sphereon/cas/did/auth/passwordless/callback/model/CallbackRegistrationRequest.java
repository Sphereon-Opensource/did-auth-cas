package com.sphereon.cas.did.auth.passwordless.callback.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class CallbackRegistrationRequest {
    private final String access_token;
    private final String did;
    private final String boxPub;
    private final String pushToken;

    @JsonCreator
    public CallbackRegistrationRequest(@JsonProperty("access_token") final String access_token,
                                       @JsonProperty("access_token") final String did,
                                       @JsonProperty("access_token") final String boxPub,
                                       @JsonProperty("access_token") final String pushToken) {
        this.access_token = access_token;
        this.did = did;
        this.boxPub = boxPub;
        this.pushToken = pushToken;
    }
}
