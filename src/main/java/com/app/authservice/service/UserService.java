package com.app.authservice.service;

import com.app.authservice.exception.AppException;
import com.app.authservice.model.User;
import com.app.authservice.model.UserPrincipal;
import com.app.authservice.repository.UserRepository;
import com.app.authservice.validation.EmailValidator;
import com.app.authservice.validation.PasswordValidator;
import com.app.authservice.validation.UserValidator;
import com.app.authservice.validation.UsernameValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;
import java.util.Optional;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\W).{6,}$");

    public User save(User user) {
        validateUserFields(user);
        setupValidationChain().validate(user);
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Iterable<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(int id) {
        return userRepository.findById(id);
    }

    public User update(int id, User user) {
        User existingUser = userRepository.findById(id).orElse(null);
        if (existingUser != null) {
            existingUser.setUsername(user.getUsername());
            existingUser.setPassword(encoder.encode(user.getPassword()));
            return userRepository.save(existingUser);
        }
        return null;
    }

    public void delete(int id) {
        userRepository.deleteById(id);
    }

    public UserDetails convertToUserDetails(User user) {
        return new UserPrincipal(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return new UserPrincipal(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    private void validateUserFields(User user) {
        logger.debug("Validating user fields: {}", user);
        if (user.getUsername() == null || user.getEmail() == null || user.getPassword() == null) {
            throw new AppException.ValidationException("Username, email, and password cannot be null");
        }
        if (!EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            throw new AppException.ValidationException("Invalid email format");
        }
        if (!isEmailDomainValid(user.getEmail())) {
            throw new AppException.ValidationException("Invalid email domain");
        }
        if (!PASSWORD_PATTERN.matcher(user.getPassword()).matches()) {
            throw new AppException.ValidationException("Password must be at least 6 characters long and include lowercase, uppercase, and special characters");
        }
    }

    private boolean isEmailDomainValid(String email) {
        String domain = email.substring(email.indexOf("@") + 1);
        try {
            Hashtable<String, String> env = new Hashtable<>();
            env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
            DirContext ctx = new InitialDirContext(env);
            ctx.getAttributes(domain, new String[]{"MX"});
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private UserValidator setupValidationChain() {
        UserValidator usernameValidator = new UsernameValidator();
        UserValidator emailValidator = new EmailValidator();
        UserValidator passwordValidator = new PasswordValidator();

        usernameValidator.setNextValidator(emailValidator);
        emailValidator.setNextValidator(passwordValidator);

        return usernameValidator;
    }
}