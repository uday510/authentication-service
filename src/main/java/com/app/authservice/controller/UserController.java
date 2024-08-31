package com.app.authservice.controller;

import com.app.authservice.config.ApiRoutes;
import com.app.authservice.dto.UserDto;
import com.app.authservice.model.User;
import com.app.authservice.service.JwtService;
import com.app.authservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static com.app.authservice.mapper.UserMapper.convertToDto;

@RestController
@RequestMapping(ApiRoutes.USER_BASE)
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @PostMapping(ApiRoutes.REGISTER)
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user) {
        User savedUser = userService.save(user);
        UserDto userDto = convertToDto(savedUser);
        Map<String, Object> response = new HashMap<>();
        response.put("user", userDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping(ApiRoutes.LOGIN)
    public ResponseEntity<Map<String, String>> login(@RequestBody User user) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

        if (authentication.isAuthenticated()) {
            UserDetails userDetails = userService.convertToUserDetails(user);
            String token = jwtService.generateAccessToken(userDetails);
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Not Authenticated");
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @GetMapping()
    public ResponseEntity<Map<String, Object>> getUserById() {
        // Get the authenticated user details from the SecurityContextHolder
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        // Fetch the user details using the username
        User user = userService.findByUsername(username);
        UserDto userDto = convertToDto(user);
        Map<String, Object> response = new HashMap<>();
        response.put("user", userDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping()
    public ResponseEntity<Map<String, Object>> updateUser(@RequestBody User user) {
        // Get the authenticated user details from the SecurityContextHolder
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        // Fetch the user details using the username
        User existingUser = userService.findByUsername(username);
        if (existingUser != null) {
            User updatedUser = userService.update(existingUser.getUserId(), user);
            Map<String, Object> response = new HashMap<>();
            response.put("user", updatedUser);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping()
    public ResponseEntity<Map<String, String>> deleteUser() {
        // Get the authenticated user details from the SecurityContextHolder
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        // Fetch the user details using the username
        User user = userService.findByUsername(username);
        if (user != null) {
            userService.delete(user.getUserId());
            Map<String, String> response = new HashMap<>();
            response.put("message", "User deleted successfully");
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        }
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "User not found");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/home")
    public ResponseEntity<Map<String, String>> home() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Hello, World!");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}