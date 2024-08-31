
package com.app.authservice.validation;

import com.app.authservice.model.User;

public class PasswordValidator extends UserValidator {
    @Override
    public void validate(User user) {
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        super.validate(user);
    }
}