package com.example.StarterPack.users;

import java.security.SecureRandom;

import com.example.StarterPack.entity.AbstractEntity;
import com.example.StarterPack.utils.Client;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
@Client
public class VerificationCode extends AbstractEntity {
    private String code;
    @Setter
    private boolean emailSent = false;
    @OneToOne
    private User user;

    public VerificationCode(User user) {
        this.user = user;
        SecureRandom random = new SecureRandom();
        int number = 100000 + random.nextInt(900000);
        String result = String.valueOf(number);
        this.code = result;
    }
}
