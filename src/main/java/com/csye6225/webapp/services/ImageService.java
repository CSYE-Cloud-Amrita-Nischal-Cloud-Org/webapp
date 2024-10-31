package com.csye6225.webapp.services;

import com.csye6225.webapp.entity.ImageEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

public interface ImageService {

    ImageEntity addImage(MultipartFile image, UUID uuid);

    Optional<ImageEntity> getImage(UUID uuid);

    boolean isImageValid(MultipartFile image);

    String uploadFile(MultipartFile file, String fileName, UUID userId);

    void deleteFile(ImageEntity imageEntity);
}
