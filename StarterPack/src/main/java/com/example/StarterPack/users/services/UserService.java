package com.example.StarterPack.users.services;

import java.io.IOException;

import org.jobrunr.scheduling.BackgroundJobRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.StarterPack.s3.UploadedFile;
import com.example.StarterPack.s3.Repositories.UploadedFileRepository;
import com.example.StarterPack.s3.Services.FileUploadService;
import com.example.StarterPack.users.PasswordResetToken;
import com.example.StarterPack.users.User;
import com.example.StarterPack.users.VerificationCode;
import com.example.StarterPack.users.data.UpdateUserPasswordRequest;
import com.example.StarterPack.users.data.UpdateUserRequest;
import com.example.StarterPack.users.data.createUserRequest;
import com.example.StarterPack.users.data.userResponse;
import com.example.StarterPack.users.jobs.SendResetPasswordEmailJob;
import com.example.StarterPack.users.jobs.SendWelcomeEmailJob;
import com.example.StarterPack.users.repositories.PasswordResetTokenRepository;
import com.example.StarterPack.users.repositories.UserRepository;
import com.example.StarterPack.users.repositories.VerificationCodeRepository;
import com.example.StarterPack.utils.Exception.ApiException;
import com.example.StarterPack.utils.Security.SecurityUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
// đọc luồng nhá: https://chatgpt.com/g/g-p-6996d9f3e7708191a14dd4a117e60142-spring/c/69be994a-eebc-8321-9a51-df90da3797da
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository _UserRepository;
    private final VerificationCodeRepository _VerificationCodeRepository;
    private final PasswordEncoder _PasswordEncoder;
    private final FileUploadService _FileUploadService;
    private final UploadedFileRepository _UploadedFileRepository;
    private userResponse convertToDto(User user) {
        return userResponse.builder()
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
            .build();
    }

    public userResponse createUser(createUserRequest request) {
        User user = User.builder()
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(encodePassword(request.getPassword()))
                .verified(false)
                .build();
        _UserRepository.save(user);
        sendVerificationEmail(user);
        return convertToDto(user);
    }

    private void sendVerificationEmail(User user) {
        System.out.println("----------------------sendVerificationEmail-----------------------");
        VerificationCode verificationCode = new VerificationCode(user);
        user.setVerificationCode(verificationCode);
        _VerificationCodeRepository.save(verificationCode);
        /* 
        SendWelcomeEmailJob sendWelcomeEmailJob = new SendWelcomeEmailJob(user.getId()):
        Nó là “message” gửi cho hệ thống JobRunr, Nó giống như bạn gửi:
        "Hey hệ thống, hãy gửi email cho userId = 123"
         */
        SendWelcomeEmailJob sendWelcomeEmailJob = new SendWelcomeEmailJob(user.getId());
        // Lưu job vào DB, Đánh dấu trạng thái: ENQUEUED
        System.out.println("lưu vào db");
        BackgroundJobRequest.enqueue(sendWelcomeEmailJob);
    }
    @Transactional
    public void verifyEmail(String code) {
        System.out.println("--------------------------UserService.verifyEmail------------------");
        log.info("code: {}", code);
        VerificationCode verificationCode = _VerificationCodeRepository.findByCode(code)
                .orElseThrow(() -> ApiException.builder().status(404).message("Invalid token").build());
        User user = verificationCode.getUser();
        if (user.getVerified() == true) {
            throw new RuntimeException("User already verified");
        }
        log.info("verificationCode: {} id: {}", verificationCode.getCode(), verificationCode.getId());
        System.out.println("User: " + user);
        // 1. Cập nhật trạng thái user
        user.setVerified(true);
        // 2. QUAN TRỌNG: Ngắt liên kết để tránh lỗi Transient/Orphan
        user.setVerificationCode(null);
        // 3. Lưu user trước
        _UserRepository.save(user);
        // 4. Xóa code sau khi đã gỡ liên kết khỏi User
        _VerificationCodeRepository.delete(verificationCode);
    }
    
    @Transactional
    public void forgotPassword(String email) {
        User user = _UserRepository.findByEmail(email);
        if (user == null) {
            throw ApiException.builder().status(404).message("User not found").build();
        }
        PasswordResetToken passwordResetToken = new PasswordResetToken(user);
        passwordResetTokenRepository.save(passwordResetToken);
        SendResetPasswordEmailJob sendResetPasswordEmailJob = new SendResetPasswordEmailJob(passwordResetToken.getId());
        BackgroundJobRequest.enqueue(sendResetPasswordEmailJob);
    }

    @Transactional
    public void resetPassword(UpdateUserPasswordRequest request) {
        // tìm token reset password
        PasswordResetToken passwordResetToken = passwordResetTokenRepository
                .findByToken(request.getPasswordResetToken())
                .orElseThrow(
                        () -> ApiException.builder().status(404).message("Password reset token not found").build());
        // nếu token hết hạn thì nhót
        if (passwordResetToken.isExpired()) {
            throw ApiException.builder().status(400).message("Password reset token is expired").build();
        }
        // tìm user và set password mới
        User user = passwordResetToken.getUser();
        user.setPassword(encodePassword(request.getPassword()));
        _UserRepository.save(user);
    }

    private String encodePassword(String password) {
        return _PasswordEncoder.encode(password);
    }
    
    @Transactional
    public userResponse update(UpdateUserRequest request) {
        User user = SecurityUtil.getAuthenticatedUser();
        user = _UserRepository.getReferenceById(user.getId());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user = _UserRepository.save(user);
        return userResponse.builder().firstName(user.getFirstName()).lastName(user.getLastName()).build();
    }
    
    @Transactional
    public userResponse updatePassword(UpdateUserPasswordRequest request) {
        User user = SecurityUtil.getAuthenticatedUser();
        if (user.getPassword() != null && !_PasswordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw ApiException.builder().status(400).message("Wrong password").build();
        }

        user.setPassword(encodePassword(request.getPassword()));
        _UserRepository.save(user);
        return userResponse.builder().firstName(user.getFirstName()).lastName(user.getLastName()).email(user.getEmail())
                .build();
    }
    
    public userResponse updateProfilePicture(MultipartFile file) {
        System.out.println("----------------------UserService.updateProfilePicture----------------------");
            User user = SecurityUtil.getAuthenticatedUser();
            UploadedFile uploadedFile = new UploadedFile(file.getOriginalFilename(), file.getSize(), user);
        try {
            String url = _FileUploadService.uploadFile(
                uploadedFile.buildPath("profile-picture"),
                    file.getBytes());
            System.out.println("URL: "+ url);
            uploadedFile.onUploaded(url);
            user.setProfileImageUrl(url);
            _UserRepository.save(user);
            _UploadedFileRepository.save(uploadedFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return  userResponse.builder().email(user.getEmail()).profileImageUrl(user.getProfileImageUrl()).build();
    }
}
