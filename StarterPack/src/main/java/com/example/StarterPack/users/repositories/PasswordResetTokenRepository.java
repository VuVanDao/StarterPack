package com.example.StarterPack.users.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.StarterPack.users.PasswordResetToken;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long>{
    @Query("SELECT prt FROM PasswordResetToken prt WHERE prt.token = ?1")
    Optional<PasswordResetToken> findByToken(String passwordResetToken);
}
