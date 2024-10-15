package com.csye6225.webapp.service.impl;

import com.csye6225.webapp.entity.UserEntity;
import com.csye6225.webapp.models.User;
import com.csye6225.webapp.repository.UserRepository;
import com.csye6225.webapp.services.impl.UserServiceImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    private AutoCloseable closeable;

    @InjectMocks
    private UserServiceImpl _userService;

    @Mock
    private UserRepository _userRepository;

    @BeforeClass
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterClass
    public void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void isEmailValid_Success() {
        String email = "test@test.com";
        boolean result = _userService.isEmailValid(email);
        assertTrue(result, "Email is valid");
    }

    @Test
    public void isEmailValid_Failure() {
        String email = "test@tes";
        boolean result = _userService.isEmailValid(email);
        assertFalse(result, "Email is invalid");
    }

    @Test
    public void isPasswordValid_Failure() {
        String password = "test";
        boolean result = _userService.isPasswordValid(password);
        assertFalse(result, "Password is invalid");
    }

    @Test
    public void isPasswordValid_Success() {
        String password = "test-test";
        boolean result = _userService.isPasswordValid(password);
        assertTrue(result, "Password is valid");
    }

    @Test
    public void getUserByEmail_Success() {
        String email = "test@test.com";

        when(_userRepository.findByemail(email)).thenReturn(getUserEntity(email));

        UserEntity expectedUser = _userService.getUserByEmail(email);
        assertNotNull(expectedUser, "User should be returned");
        assertEquals(expectedUser, _userService.getUserByEmail(email), "User email should match");
    }

    @Test
    public void getUserByEmail_Failure() {
        String email = "test@test.com";
        UserEntity expectedUser = _userService.getUserByEmail(email);

        when(_userRepository.findByemail(email)).thenReturn(null);
        assertNull(expectedUser, "User should be null");
    }

    @Test
    public void createUser_Success() {
        // Actual user
        User user = User.builder()
                .firstName("first")
                .lastName("last")
                .email("firstlast.com")
                .password("password")
                .build();

        // Expected UserEntity
        UserEntity actualUserEntity = getUserEntity("first@last.com");

        when(_userRepository.save(any(UserEntity.class))).thenReturn(actualUserEntity);

        UserEntity expectedUser = _userService.createUser(user);

        assertNotNull(expectedUser, "User should be created");
        assertEquals(expectedUser.getEmail(), user.getEmail(), "User should match");
        assertNull(expectedUser.getPassword(), "Password should be null");
    }

    private UserEntity getUserEntity(String email) {
        return UserEntity.builder().email(email).password("test").build();
    }
}
