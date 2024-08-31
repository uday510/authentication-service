package com.app.authservice.validation;

import com.app.authservice.model.User;

public class EmailValidator extends UserValidator {
    @Override
    public void validate(User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        super.validate(user);
    }
}