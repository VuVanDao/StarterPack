package com.example.StarterPack.auth.Services;

import java.util.Collection;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

import com.example.StarterPack.auth.data.LoginRequest;
import com.example.StarterPack.users.User;
import com.example.StarterPack.users.data.userResponse;
import com.example.StarterPack.users.repositories.UserRepository;
import com.example.StarterPack.utils.Security.SecurityUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    // Đây là “cục xử lý login” chính của Spring , Xác thực login
    private final AuthenticationManager authenticationManager;
    // Lưu và lấy SecurityContext giữa các request, Đây là nơi lưu trạng thái đăng nhập, Lưu trạng thái login
    private SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();
    // Xử lý logout, Xóa trạng thái login
    SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
    
    public void login(HttpServletRequest request,
      HttpServletResponse response,
      LoginRequest body
    ) throws AuthenticationException {
        System.out.println("------------------------------AuthService.login-------------------------");
        // Tạo token đăng nhập
        /*
            Đây là object đại diện cho thông tin login
            principal = email, credentials = password, trạng thái = chưa xác thực
         */
        UsernamePasswordAuthenticationToken token = UsernamePasswordAuthenticationToken.unauthenticated(body.getEmail(),
                body.getPassword());
        System.out.println("UsernamePasswordAuthenticationToken: " + token);
        // Xác thực
        /*
            Spring sẽ:
                Gọi UserDetailsService
                Load user từ DB
                So sánh password (qua PasswordEncoder)
                Nếu đúng → trả về Authentication đã xác thực
            Sau khi thành công:
                authentication.isAuthenticated() = true
                Có chứa:
                user info
                roles (ROLE_USER, ROLE_ADMIN)
        */
        Authentication authentication = authenticationManager.authenticate(token);
        System.out.println("Authentication: " + authentication);
        // Lấy SecurityContext strategy
        // Đây là cách Spring lưu context của user hiện tại
        SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
        // Tạo context mới
        // Tạo 1 “container” chứa thông tin đăng nhập
        SecurityContext context = securityContextHolderStrategy.createEmptyContext();
        // Gắn authentication vào context, sau dòng này ,context đã chứa user đã login
        context.setAuthentication(authentication);
        // Set context vào hệ thống
        securityContextHolderStrategy.setContext(context);
        // Lưu context (QUAN TRỌNG)
        /*
            Đây là bước giúp giữ trạng thái login giữa các request
        */
        securityContextRepository.saveContext(context, request, response);
    }
    
    @Transactional
    public userResponse getSession(HttpServletRequest request) {
        User user = SecurityUtil.getAuthenticatedUser();
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities();
        return new userResponse(user, authorities);
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        this.logoutHandler.logout(request, response, authentication);
    }
}
