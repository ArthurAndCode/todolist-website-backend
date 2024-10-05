package com.arthur.code.todolist_website_backend.service.userService;

import com.arthur.code.todolist_website_backend.data.UserRepository;
import com.arthur.code.todolist_website_backend.model.User;
import com.arthur.code.todolist_website_backend.service.TaskService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final TaskService taskService;
    private final Map<String, String> passwordResetTokens;
    private final Map<String, String> rememberMeCookieTokens;

    public UserService(UserRepository userRepository, EmailService emailService, TaskService taskService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.taskService = taskService;
        this.passwordResetTokens = new HashMap<>();
        this.rememberMeCookieTokens = new HashMap<>();
    }

    public User getUser(String email, String password) {
        User user = findUserByEmailOrThrow(email);
        if (!user.getPassword().equals(password)) {
            throw new IllegalStateException("Incorrect password.");
        }
        return user;
    }

    public void addUser(User user) {
        ensureUserDoesNotExistOrThrow(user.getEmail());
        userRepository.save(user);
    }

    public void deleteUserAndTasks(Long userId) {
        taskService.deleteAllTasksByUserId(userId);
        userRepository.deleteById(userId);
    }

    public User getUserByToken(String token) {
        String email = rememberMeCookieTokens.get(token);
        if (email == null) {
            throw new IllegalStateException("Invalid token.");
        }
        return findUserByEmailOrThrow(email);
    }

    public void setRememberMeCookie(HttpServletResponse response, String email) {
        String token = generateTokenForUser(email);
        Cookie cookie = new Cookie("rememberMeToken", token);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
        response.addCookie(cookie);
    }


    public void clearRememberMeCookie(HttpServletResponse response, String token) {
        rememberMeCookieTokens.remove(token);
        Cookie cookie = new Cookie("rememberMeToken", null);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    public void handleResetPasswordRequest(String email) {
        findUserByEmailOrThrow(email);
        String token = UUID.randomUUID().toString();
        savePasswordResetToken(email, token);
        emailService.sendPasswordResetLink(email, token);
    }

    public void resetPassword(String token) {
        String email = validateResetToken(token);
        String newPassword = generateRandomPassword();
        updatePassword(email, newPassword);
        emailService.sendTemporaryPassword(email, newPassword);
        passwordResetTokens.remove(token);
    }

    public void updatePassword(String email, String newPassword) {
        userRepository.updatePassword(email, newPassword);
    }

    public void updateEmail(String email, String newEmail) {
        ensureUserDoesNotExistOrThrow(newEmail);
        userRepository.updateEmail(email, newEmail);
    }

    private User findUserByEmailOrThrow(String email) {
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found."));
    }

    private void ensureUserDoesNotExistOrThrow(String email) {
        if (userRepository.findUserByEmail(email).isPresent()) {
            throw new IllegalStateException("Email already taken.");
        }
    }
    public void savePasswordResetToken(String email, String token) {
        passwordResetTokens.put(token, email);
    }

    private String validateResetToken(String token) {
        String email = passwordResetTokens.get(token);
        if (email == null) {
            throw new IllegalStateException("Invalid or expired token.");
        }
        return email;
    }
    public String generateTokenForUser(String email) {
        String token = UUID.randomUUID().toString();
        rememberMeCookieTokens.put(token, email);
        return token;
    }

    private String generateRandomPassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}


