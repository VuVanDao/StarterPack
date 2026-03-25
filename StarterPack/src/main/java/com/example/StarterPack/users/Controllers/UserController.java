package com.example.StarterPack.users.Controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import com.example.StarterPack.config.ApplicationProperties;
import com.example.StarterPack.users.data.ForgotPasswordRequest;
import com.example.StarterPack.users.data.UpdateUserPasswordRequest;
import com.example.StarterPack.users.data.UpdateUserRequest;
import com.example.StarterPack.users.data.createUserRequest;
import com.example.StarterPack.users.data.userResponse;
import com.example.StarterPack.users.services.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService _UserService;
    private final ApplicationProperties _ApplicationProperties;
    @PostMapping
    public ResponseEntity<userResponse> createUser(@Valid @RequestBody createUserRequest request) {
        userResponse userResponse = _UserService.createUser(request);
        return ResponseEntity.ok(userResponse);
    }
    /**
    * Verify the email of the user, redirect to the login page.
    */
    @GetMapping("/verify-email")
    public RedirectView verifyEmail(@RequestParam String token) {
        System.out.println("--------------------------UserController.verifyEmail------------------");
        System.out.println("token:" + token);
        _UserService.verifyEmail(token);
        return new RedirectView(_ApplicationProperties.getLoginPageUrl());
    }

    /**
        * Request a password reset email
   */
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest req) {
        _UserService.forgotPassword(req.getEmail());
        return ResponseEntity.ok().build();
    }

    /**
     * Reset the password of an existing user, uses the password reset token
     * <p>
     * Only allowed with the password reset token
    */
    @PatchMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(
            @Valid @RequestBody UpdateUserPasswordRequest requestDTO) {
        _UserService.resetPassword(requestDTO);
        return ResponseEntity.ok().build();
    }
    /**
     * Update an existing user.
     * <p>
     * Only allowed to self.
    */
    @PutMapping("/{id}")
    public ResponseEntity<userResponse> updateUser(@Valid @RequestBody UpdateUserRequest request) {
        userResponse user = _UserService.update(request);
        return ResponseEntity.ok(user);
    }
    /**
     * Update the password of an existing user.
     * <p>
     * Only allowed with the correct old password
     */
    @PatchMapping("/password")
    public ResponseEntity<userResponse> updatePassword(
            @Valid @RequestBody UpdateUserPasswordRequest requestDTO) {
        userResponse user = _UserService.updatePassword(requestDTO);
        return ResponseEntity.ok(user);
    }
    @PatchMapping("/{id}/profile-picture")
    public ResponseEntity<userResponse> updateProfilePicture(
            @PathVariable Long id, @RequestParam("file") MultipartFile file) {
        System.out.println("---------------------------UserController.updateProfilePicture----------------------");
            System.out.println("file: "+file);
        userResponse user = _UserService.updateProfilePicture(file);
        return ResponseEntity.ok(user);
    }
}
