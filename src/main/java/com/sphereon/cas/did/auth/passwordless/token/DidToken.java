package com.sphereon.cas.did.auth.passwordless.token;

public class DidToken {
    private final String username;
    private String requestToken;
    private String responseToken;

    public DidToken(final String username, final String requestToken) {
        this.username = username;
        this.requestToken = requestToken;
        this.responseToken = null;
    }

    public DidToken(final String username, final String requestToken, final String responseToken){
        this.username = username;
        this.requestToken = requestToken;
        this.responseToken = responseToken;
    }

    public String getResponseToken() {
        return responseToken;
    }

    public void setResponseToken(String responseToken) {
        this.responseToken = responseToken;
    }

    public void setRequestToken(String requestToken) {
        this.requestToken = requestToken;
    }

    public String getRequestToken() {
        return requestToken;
    }

    public String getUsername() {
        return username;
    }
}
