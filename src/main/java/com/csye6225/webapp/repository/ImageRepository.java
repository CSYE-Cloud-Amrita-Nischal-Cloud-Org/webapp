package com.csye6225.webapp.repository;

import com.csye6225.webapp.entity.ImageEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

@Transactional
public interface ImageRepository extends CrudRepository<ImageEntity, Integer> {

    Optional<ImageEntity> findByUserId(UUID userId);
}
