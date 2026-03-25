package com.example.StarterPack.users.data;

import com.example.StarterPack.utils.Client;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Client
public class UpdateUserRequest {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
}
