package com.sphereon.cas.did.auth.passwordless.repository;

import com.sphereon.cas.did.auth.passwordless.repository.model.RegistrationRequest;

import java.util.Optional;

public interface RegistrationRepository {

    Optional<RegistrationRequest> findRegistrationRequest(String registrationId);

    void deleteRegistrationRequest(String registrationId);

    void saveRegistrationRequest(String registrationId, RegistrationRequest registrationRequest);
}
