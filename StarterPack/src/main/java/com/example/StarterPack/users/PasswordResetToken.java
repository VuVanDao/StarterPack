package com.example.StarterPack.users;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import com.example.StarterPack.entity.AbstractEntity;
import com.example.StarterPack.utils.Client;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "password_reset_token")
@Getter
@NoArgsConstructor
@Client
public class PasswordResetToken extends AbstractEntity {

    private String token;
    private Boolean emailSent = false;
    private LocalDateTime expiresAt;

    @ManyToOne
    private User user;

    public PasswordResetToken(User user) {
        this.user = user;
        SecureRandom random = new SecureRandom();
        int number = 100000 + random.nextInt(900000);
        String result = String.valueOf(number);
        this.token = result;
    }

    public Boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public void onEmailSent() {
        this.emailSent = true;
        this.expiresAt = LocalDateTime.now().plusMinutes(10);
    }
}