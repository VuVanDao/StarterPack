package com.example.StarterPack.users.jobs.handler;

import java.util.List;

import org.jobrunr.jobs.lambdas.JobRequestHandler;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.example.StarterPack.config.ApplicationProperties;
import com.example.StarterPack.users.PasswordResetToken;
import com.example.StarterPack.users.User;
import com.example.StarterPack.users.jobs.SendResetPasswordEmailJob;
import com.example.StarterPack.users.repositories.PasswordResetTokenRepository;
import com.example.StarterPack.users.services.EmailService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SendResetPasswordEmailJobHandler implements JobRequestHandler<SendResetPasswordEmailJob> {
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;
    private final ApplicationProperties applicationProperties;
    private final SpringTemplateEngine templateEngine;

    @Override
    @Transactional
    public void run(SendResetPasswordEmailJob sendResetPasswordEmailJob) throws Exception {
        System.out.println("----------------------SendResetPasswordEmailJobHandler.RUNNNNNNNNNNNNN-----------------------");
        PasswordResetToken resetToken = passwordResetTokenRepository.findById(sendResetPasswordEmailJob.getTokenId())
            .orElseThrow(() -> new IllegalArgumentException("Token not found"));
        if (!resetToken.getEmailSent()) {
            sendResetPasswordEmail(resetToken.getUser(), resetToken);
        }
    }

    private void sendResetPasswordEmail(User user, PasswordResetToken token) {
        System.out.println("----------------------SendResetPasswordEmailJobHandler.sendResetPasswordEmail-----------------------");
        String link = applicationProperties.getBaseUrl() + "/auth/reset-password?token=" + token.getToken();
        Context thymeleafContext = new Context();
        thymeleafContext.setVariable("user", user);
        thymeleafContext.setVariable("link", link);
        String htmlBody = templateEngine.process("password-reset", thymeleafContext);
        emailService.sendHtmlMessage(List.of(user.getEmail()), "Password reset requested", htmlBody);
        token.onEmailSent();
        passwordResetTokenRepository.save(token);
    }
}
