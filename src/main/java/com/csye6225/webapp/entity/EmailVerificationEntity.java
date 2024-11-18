package com.csye6225.webapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"email", "token", "expiration_time"})
@Table(name = "email_verifications",  uniqueConstraints= @UniqueConstraint(columnNames={"token"}))
public class EmailVerificationEntity {

    @Id
    @Column(name = "token")
    @JsonProperty("token")
    String token;

    @Column(name = "email")
    @JsonProperty("email")
    String email;

    @Column(name = "expiration_time")
    @JsonIgnore
    String expirationTime;
}
