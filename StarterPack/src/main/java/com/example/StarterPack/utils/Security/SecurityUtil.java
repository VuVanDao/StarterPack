package com.example.StarterPack.utils.Security;

import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

import com.example.StarterPack.users.User;
import com.example.StarterPack.utils.Exception.ApiException;

import lombok.extern.slf4j.Slf4j;
// đọc thêm: https://chatgpt.com/g/g-p-6996d9f3e7708191a14dd4a117e60142-spring/c/69c0056f-5280-839a-935c-5dc12d2694c8
@Slf4j
public class SecurityUtil {
  private static final SecurityContextRepository securityContextRepository =
    new HttpSessionSecurityContextRepository();

  /**
   * Get the authenticated user from the SecurityContextHolder
   * @throws com.example.backend.util.exception.ApiException if the user is not found in the SecurityContextHolder
   */
  public static User getAuthenticatedUser() {
      /*
        ✔ Dùng khi bắt buộc phải login
        ✔ Không có user → throw exception luôn
      */
    System.out.println("-----------------------SecurityUtil.getAuthenticatedUser--------------------------");
    // Lấy object principal từ SecurityContext
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    System.out.println("Principal: "+principal);
    if (principal instanceof User user) {
      return user;
    }else {
      log.error("User requested but not found in SecurityContextHolder");
      throw ApiException.builder().status(401).message("Authentication required").build();
    }
  }

  public static Optional<User> getOptionalAuthenticatedUser() {
    // Hàm này giống hàm trên, nhưng an toàn hơn:
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (principal instanceof User user) {
      return Optional.of(user);
    } else {
      return Optional.empty();
    }
  }
}