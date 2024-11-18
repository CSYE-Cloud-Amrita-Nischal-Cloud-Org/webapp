package com.csye6225.webapp.repository;

import com.csye6225.webapp.entity.EmailVerificationEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface EmailVerificationsRepository extends CrudRepository<EmailVerificationEntity, Integer> {

    Optional<EmailVerificationEntity> findByToken(String token);

}
