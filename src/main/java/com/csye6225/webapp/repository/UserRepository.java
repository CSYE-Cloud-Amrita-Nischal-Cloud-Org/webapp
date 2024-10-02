package com.csye6225.webapp.repository;

import com.csye6225.webapp.entity.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.repository.CrudRepository;

@Transactional
public interface UserRepository extends CrudRepository<UserEntity, Integer> {

    UserEntity findByemail(String email);

}
