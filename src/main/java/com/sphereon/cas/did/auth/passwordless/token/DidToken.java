package com.sphereon.cas.did.auth.passwordless.token;

public class DidToken {
    private final String requestToken;
    private final String responseToken;
    private final boolean isResponseReceived;

    public DidToken(final String requestToken) {
        this(requestToken, null, false);
    }

    private DidToken(final String requestToken, final String responseToken, final boolean isResponseReceived) {
        this.requestToken = requestToken;
        this.responseToken = responseToken;
        this.isResponseReceived = isResponseReceived;
    }

    public String getResponseToken() {
        return responseToken;
    }

    public String getRequestToken() {
        return requestToken;
    }

    public boolean getIsResponseReceived() {
        return isResponseReceived;
    }

    public DidToken with(String responseToken) {
        return new DidToken(requestToken, responseToken, true);
    }
}
