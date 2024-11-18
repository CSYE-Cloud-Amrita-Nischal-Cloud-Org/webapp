package com.csye6225.webapp.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"file_name", "id", "url", "upload_date", "user_id"})
@Table(name = "profile_picture",  uniqueConstraints= @UniqueConstraint(columnNames={"image_id"}))
public class ImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "image_id")
    @JsonProperty("id")
    private UUID id;

    @Column(name = "user_id")
    @JsonProperty("user_id")
    UUID userId;

    @Column(name = "file_name")
    @JsonProperty("file_name")
    String fileName;

    @Column(name = "url")
    @JsonProperty("url")
    String url;

    @Column(name = "upload_date")
    @JsonProperty("upload_date")
    String uploadDate;

}
