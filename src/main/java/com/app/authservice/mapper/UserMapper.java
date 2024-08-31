package com.app.authservice.mapper;

import com.app.authservice.dto.UserDto;
import com.app.authservice.model.User;

public class UserMapper {
    public static UserDto convertToDto(User user) {
        if (user == null) {
            return null;
        }
        UserDto userDto = new UserDto();
        userDto.setUserId(user.getUserId());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        userDto.setEnabled(user.isEnabled());
        userDto.setAccountNonExpired(user.isAccountNonExpired());
        userDto.setCredentialsNonExpired(user.isCredentialsNonExpired());
        userDto.setAccountNonLocked(user.isAccountNonLocked());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setPhoneNumber(user.getPhoneNumber());
        userDto.setRole(user.getRole());
        return userDto;
    }
}