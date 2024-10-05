package com.arthur.code.todolist_website_backend.controller;


import com.arthur.code.todolist_website_backend.model.User;
import com.arthur.code.todolist_website_backend.service.userService.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/v1/user")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class UserController {

    private static final String PASSWORD_RESET_SUCCESS_MESSAGE =
            "Your password has been successfully reset. A new temporary password has been sent to your email. "
                    + "Please log in using the temporary password, and for security purposes, we strongly recommend changing it immediately after logging in.";

    private static final String USER_REGISTERED_SUCCESS_MESSAGE =
            "Your account has been successfully registered. Welcome aboard! You can now log in and start using the service.";

    private static final String USER_DELETED_SUCCESS_MESSAGE =
            "The user account and all associated tasks have been successfully deleted.";

    private static final String LOGGED_OUT_SUCCESS_MESSAGE =
            "You have been successfully logged out. We hope to see you back soon!";

    private static final String NO_TOKEN_FOUND_MESSAGE =
            "No valid session token was found. Please log in to access your account.";

    private static final String PASSWORD_CHANGED_SUCCESS_MESSAGE =
            "Success! Your password has been updated. Please make sure to use the new password for future logins.";

    private static final String EMAIL_CHANGED_SUCCESS_MESSAGE =
            "Success! Your email has been successfully updated. Please use your new email for future communications and logins.";

    private static final String PASSWORD_RESET_LINK_SENT_MESSAGE =
            "A password reset link has been sent to your email. Please check your inbox and follow the instructions to reset your password.";

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<User> getUser(@RequestParam String email, @RequestParam String password, HttpServletResponse response, @RequestParam boolean rememberMe) {
        User user = userService.getUser(email, password);
        if (rememberMe) {
            userService.setRememberMeCookie(response, email);
        }
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        userService.addUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(USER_REGISTERED_SUCCESS_MESSAGE);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        userService.deleteUserAndTasks(userId);
        return ResponseEntity.ok(USER_DELETED_SUCCESS_MESSAGE);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getUserByToken(@CookieValue(value = "rememberMeToken", required = false) String token) {
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(NO_TOKEN_FOUND_MESSAGE);
        }
        User user = userService.getUserByToken(token);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@CookieValue(value = "rememberMeToken", required = false) String token, HttpServletResponse response) {
        if (token == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(NO_TOKEN_FOUND_MESSAGE);
        }
        userService.clearRememberMeCookie(response, token);
        return ResponseEntity.ok(LOGGED_OUT_SUCCESS_MESSAGE);
    }

    @PutMapping("/password")
    public ResponseEntity<String> changePassword(@RequestParam(name = "email") String email, @RequestParam(name = "newPassword") String password) {
        userService.updatePassword(email, password);
        return ResponseEntity.ok(PASSWORD_CHANGED_SUCCESS_MESSAGE);
    }

    @PutMapping("/email")
    public ResponseEntity<String> changeEmail(@RequestParam(name = "email") String email, @RequestParam(name = "newEmail") String newEmail) {
        userService.updateEmail(email, newEmail);
        return ResponseEntity.ok(EMAIL_CHANGED_SUCCESS_MESSAGE);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> sendPasswordResetLink(@RequestParam String email) {
        userService.handleResetPasswordRequest(email);
        return ResponseEntity.ok(PASSWORD_RESET_LINK_SENT_MESSAGE);
    }

    @GetMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token) {
        userService.resetPassword(token);
        return ResponseEntity.ok(PASSWORD_RESET_SUCCESS_MESSAGE);
    }
}




