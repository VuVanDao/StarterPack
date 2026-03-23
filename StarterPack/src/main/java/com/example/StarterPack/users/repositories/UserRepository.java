package com.example.StarterPack.users.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.StarterPack.users.User;

public interface UserRepository extends JpaRepository<User,Long>{
    // @Query("SELECT u FROM User u WHERE u.email = :email")
    User findByEmail(String email);
}
