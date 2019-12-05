package com.sphereon.cas.did.auth.passwordless.token;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DidTokenTest {

    @Test
    public void didTokenShouldUpdateIsResponseRecievedWhenAResponseIsAdded(){
        var didToken = new DidToken("request");
        assertFalse(didToken.isResponseReceived());
        var updatedDidToken = didToken.with("response");
        assertTrue(updatedDidToken.isResponseReceived());
    }
}
