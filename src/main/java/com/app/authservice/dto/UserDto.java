package com.app.authservice.dto;

import com.app.authservice.enums.UserRole;
import lombok.Data;

@Data
public class UserDto {
    private int userId;
    private String username;
    private String email;
    private boolean enabled;
    private boolean accountNonExpired;
    private boolean credentialsNonExpired;
    private boolean accountNonLocked;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private UserRole role;
}