package com.sphereon.cas.did.auth.passwordless;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public abstract class PasswordlessUserAccount implements Serializable {
    private static final long serialVersionUID = 5783908770607793373L;

    private String username;
    private String email;
    private String phone;
    private String name;

}
