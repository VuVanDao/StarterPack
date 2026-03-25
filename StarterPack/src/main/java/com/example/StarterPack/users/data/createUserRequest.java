package com.example.StarterPack.users.data;

import com.example.StarterPack.utils.validation.PasswordMatch;
import com.example.StarterPack.utils.validation.Unique;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// link for PasswordMatch:https://chatgpt.com/g/g-p-6996d9f3e7708191a14dd4a117e60142-spring/c/69bd3be3-f750-8322-8998-6f50d3b1e965
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@PasswordMatch(passwordField = "password",passwordConfirmationField = "passwordConfirmation")
public class createUserRequest {
    @Email
    @NotBlank(message = "email is not be empty")
    @Unique(columnName = "email", tableName = "user", message = "email must be unique")
    String email;

    @NotBlank(message = "password is not be empty")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[!@#$%^&*(),.?\":{}|<>]).{6,}$" , message = "password must at least 6 character, one special character and one upper case")
    String password;
    String passwordConfirmation;
    String firstName;
    String lastName;
    @Nullable
    Boolean verified = false;
}
