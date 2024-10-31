package com.csye6225.webapp.services.impl;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.csye6225.webapp.entity.ImageEntity;
import com.csye6225.webapp.repository.ImageRepository;
import com.csye6225.webapp.services.ImageService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class ImageServiceImpl implements ImageService {

    @Autowired
    private ImageRepository _imageRepository;

    @Autowired
    private AmazonS3 amazonS3Client;

    @Value("${aws.s3.bucket.name}")
    String bucketName;

    @Override
    public ImageEntity addImage(MultipartFile image, UUID uuid) {
        // Upload the file
        String url = uploadFile(image, image.getOriginalFilename(), uuid);

        // Return image entity
        Instant now = Instant.now();
        ImageEntity imageEntity = ImageEntity.builder()
                .id(uuid)
                .url(url)
                .fileName(image.getOriginalFilename())
                .uploadDate(now.toString())
                .userId(uuid)
                .build();
        imageEntity = _imageRepository.save(imageEntity);

        return imageEntity;
    }

    @Override
    public Optional<ImageEntity> getImage(UUID uuid) {
        return _imageRepository.findByUserId(uuid);
    }

    @Override
    public boolean isImageValid(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            return false;
        }
        // Get type of file
        String imageContentType = image.getContentType();
        log.info("File Content Type: {}", imageContentType);
        return imageContentType != null &&
                (imageContentType.equals("image/png") ||
                        imageContentType.equals("image/jpeg") ||
                        imageContentType.equals("image/jpg"));
    }

    @SneakyThrows
    @Override
    public String uploadFile(MultipartFile image, String fileName, UUID userId) {
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(image.getSize());
            fileName = StringUtils.isEmpty(fileName) ? "untitled" : fileName;
            String s3FileName = userId + "/" + fileName.replace(" ", "-");
            InputStream inputStream = image.getInputStream();
            amazonS3Client.putObject(bucketName, s3FileName, inputStream, metadata);
            inputStream.close();
            return bucketName + "/" + s3FileName;
        } catch (AmazonServiceException serviceException) {
            log.info("AmazonServiceException: {}", serviceException.getMessage());
            throw serviceException;
        } catch (AmazonClientException clientException) {
            log.info("AmazonClientException Message: {}", clientException.getMessage());
            throw clientException;
        }
    }

    @Override
    public void deleteFile(ImageEntity imageEntity) {
        String fileUrl = imageEntity.getUrl();
        String fileName = fileUrl.substring(fileUrl.indexOf("/") + 1);
        log.info("File to be deleted: {}/{}", bucketName, fileName);
        amazonS3Client.deleteObject(new DeleteObjectRequest(bucketName, fileName));
        _imageRepository.delete(imageEntity);
    }


}
