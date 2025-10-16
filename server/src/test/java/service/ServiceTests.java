package service;

import dataaccess.AlreadyTakenException;
import org.junit.jupiter.api.*;
import request.RegisterRequest;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceTests {


    @Test
    @Order(1)
    @DisplayName("UserService - Clear Positive")
    public void staticFilesSuccess() {
        try {
            UserService.register(new RegisterRequest("username", "password", "email@email.com"));
            UserService.clearApplication();
            Assertions.assertNull(UserService.memoryUserDAO.getUser("username"));
        }
        catch(AlreadyTakenException e) {
            Assertions.fail("Username taken");
        }
    }

}
