package com.app.authservice.validation;

import com.app.authservice.model.User;
import lombok.Setter;

@Setter
public abstract class UserValidator {
    protected UserValidator nextValidator;

    public void validate(User user) {
        if (nextValidator != null) {
            nextValidator.validate(user);
        }
    }
}