package com.sphereon.cas.did.auth.passwordless.callback.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CallbackTokenPostRequest{
    private String access_token;
}
