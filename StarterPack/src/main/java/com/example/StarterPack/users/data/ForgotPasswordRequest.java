package com.example.StarterPack.users.data;

import com.example.StarterPack.utils.Client;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
@Client
public class ForgotPasswordRequest {
    @Email
    private String email;
}
