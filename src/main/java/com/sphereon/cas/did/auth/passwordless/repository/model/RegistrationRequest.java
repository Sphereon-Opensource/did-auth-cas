package com.sphereon.cas.did.auth.passwordless.repository.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class RegistrationRequest {

    private String appId;
    private String userName;
    private String registrationId;
    private String qrCodeBase64;


    public RegistrationRequest() {
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public String getQrCodeBase64() {
        return qrCodeBase64;
    }

    public void setQrCodeBase64(String qrCodeBase64) {
        this.qrCodeBase64 = qrCodeBase64;
    }

    public RegistrationRequest userName(final String userName) {
        this.userName = userName;
        return this;
    }

    public RegistrationRequest registrationId(final String registrationId) {
        this.registrationId = registrationId;
        return this;
    }

    public RegistrationRequest qrCodeBase64(final String qrCodeBase64) {
        this.qrCodeBase64 = qrCodeBase64;
        return this;
    }

    public RegistrationRequest appId(final String appId) {
        this.appId = appId;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        RegistrationRequest that = (RegistrationRequest) o;

        return new EqualsBuilder()
                .append(appId, that.appId)
                .append(userName, that.userName)
                .append(registrationId, that.registrationId)
                .append(qrCodeBase64, that.qrCodeBase64)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(appId)
                .append(userName)
                .append(registrationId)
                .append(qrCodeBase64)
                .toHashCode();
    }
}
