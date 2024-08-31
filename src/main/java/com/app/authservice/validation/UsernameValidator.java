package com.app.authservice.validation;

import com.app.authservice.model.User;

public class UsernameValidator extends UserValidator {
    @Override
    public void validate(User user) {
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        super.validate(user);
    }
}