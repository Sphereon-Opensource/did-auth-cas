package com.sphereon.cas.did.auth.passwordless.repository;

import java.util.Optional;

public interface RegistrationQRCodeRepository {

    Optional<String> findQR(String topicId);

    void deleteQR(String topicId);

    void saveQR(String topicId, String qrCode);
}
