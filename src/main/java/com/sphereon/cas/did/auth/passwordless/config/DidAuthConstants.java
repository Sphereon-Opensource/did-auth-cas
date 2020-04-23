package com.sphereon.cas.did.auth.passwordless.config;

public final class DidAuthConstants {
    public static final class Endpoints {
        public static final class TokenCallback{
            public static final String LOGIN = "/login";
            public static final String POST_LOGIN_TOKEN = LOGIN + "/{" + Param.USERNAME + "}";
            public static final String REGISTER = "/register";
        }
    }

    public static final class Param {
        public static final String USERNAME = "username";
    }
}
