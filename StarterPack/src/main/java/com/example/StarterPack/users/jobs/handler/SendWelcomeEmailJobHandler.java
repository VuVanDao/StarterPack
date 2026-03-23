package com.example.StarterPack.users.jobs.handler;

import java.util.List;

import org.jobrunr.jobs.lambdas.JobRequestHandler;
import org.springframework.stereotype.Component;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.example.StarterPack.config.ApplicationProperties;
import com.example.StarterPack.users.User;
import com.example.StarterPack.users.VerificationCode;
import com.example.StarterPack.users.jobs.SendWelcomeEmailJob;
import com.example.StarterPack.users.repositories.UserRepository;
import com.example.StarterPack.users.repositories.VerificationCodeRepository;
import com.example.StarterPack.users.services.EmailService;
import org.thymeleaf.context.Context;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class SendWelcomeEmailJobHandler implements JobRequestHandler<SendWelcomeEmailJob> {
    private final UserRepository userRepository;
    private final VerificationCodeRepository verificationCodeRepository;
    private final SpringTemplateEngine templateEngine;
    private final EmailService emailService;
    private final ApplicationProperties applicationProperties;    
    @Override
    @Transactional
    public void run(SendWelcomeEmailJob arg0) throws Exception {
        System.out.println("----------------------SendWelcomeEmailJobHandler.RUNNNNN-----------------------");
        User user = userRepository.findById(arg0.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));
        log.info("Sending welcome email to user with id: {}", arg0.getUserId());
        System.out.println("Sending welcome email to user with id: "+ arg0.getUserId());
        if (user.getVerificationCode() != null && !user.getVerificationCode().isEmailSent()) {
            sendWelcomeEmail(user, user.getVerificationCode());
        }
    }

    private void sendWelcomeEmail(User user, VerificationCode code) {
        System.out.println("----------------------SendWelcomeEmailJobHandler.SENDWELCOMEEMAIL-----------------------");
        // Tạo link verify
        String verificationLink = applicationProperties.getBaseUrl() + "/api/users/verify-email?token=" + code.getCode();
        Context thymeleafContext = new Context();
        // Tạo template context
        thymeleafContext.setVariable("user", user);
        thymeleafContext.setVariable("verificationLink", verificationLink);
        thymeleafContext.setVariable("applicationName", applicationProperties.getApplicationName());
        // Render HTML email
        String htmlBody = templateEngine.process("welcome-email", thymeleafContext);
        // Gửi email
        emailService.sendHtmlMessage(List.of(user.getEmail()), "Welcome to our platform", htmlBody);
        System.out.println("----------------------SAVE Email Sent ----------------------");
        code.setEmailSent(true);
        verificationCodeRepository.save(code);
    }
}
