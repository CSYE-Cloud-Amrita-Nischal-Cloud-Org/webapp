package com.csye6225.webapp.entity;



import com.fasterxml.jackson.annotation.JsonIgnore;
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
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonPropertyOrder({"id", "first_name", "last_name", "email", "account_created", "account_updated", "is_verified"})
@Table(name = "users",  uniqueConstraints= @UniqueConstraint(columnNames={"user_id", "email"}))
public class UserEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "user_id")
    @JsonProperty("id")
    private UUID id;

    @Column(name = "first_name")
    @JsonProperty("first_name")
    String firstName;

    @Column(name = "last_name")
    @JsonProperty("last_name")
    String lastName;

    @Column(name = "email")
    @JsonProperty("email")
    String email;

    @Column(name = "password")
    @JsonIgnore
    String password;

    @Column(name = "account_created")
    @JsonProperty("account_created")
    String accountCreated;

    @Column(name = "account_updated")
    @JsonProperty("account_updated")
    String accountUpdated;

    @Column(name = "is_verified")
    @JsonIgnore
    Boolean isVerified;

}
