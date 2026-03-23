package com.example.StarterPack.users.data;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import com.example.StarterPack.utils.Client;
import com.example.StarterPack.utils.validation.PasswordMatch;

@Data
@PasswordMatch(passwordField = "password", passwordConfirmationField = "confirmPassword")
@Client
public class UpdateUserPasswordRequest {
  private String oldPassword;
  @NotNull
  @Length(min = 8)
  @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message = "must contain at least one uppercase letter, one lowercase letter, and one digit.")
  private String password;
  private String confirmPassword;
  private String passwordResetToken;
}