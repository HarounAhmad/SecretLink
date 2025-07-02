package io.erisdev.secretlink.service;

import io.erisdev.secretlink.domain.Secret;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecretRepository extends JpaRepository<Secret, Long> {
}
