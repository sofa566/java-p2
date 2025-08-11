package com.b2b.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${app.mail.from}")
    private String fromEmail;
    
    @Value("${app.mail.frontend-url}")
    private String frontendUrl;
    
    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("密碼重置請求 - B2B 管理系統");
            
            String resetUrl = frontendUrl + "/reset-password?token=" + resetToken;
            String emailContent = String.format(
                "您好，\n\n" +
                "我們收到了您的密碼重置請求。請點擊以下連結來重置您的密碼：\n\n" +
                "%s\n\n" +
                "此連結將在 1 小時後過期。如果您沒有請求密碼重置，請忽略此郵件。\n\n" +
                "謝謝！\n" +
                "B2B 管理系統團隊",
                resetUrl
            );
            
            message.setText(emailContent);
            
            mailSender.send(message);
            logger.info("密碼重置郵件已發送至: {}", toEmail);
            
        } catch (Exception e) {
            logger.error("發送密碼重置郵件失敗，收件人: {}", toEmail, e);
            throw new RuntimeException("郵件發送失敗: " + e.getMessage());
        }
    }
    
    public void sendWelcomeEmail(String toEmail, String userName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("歡迎加入 B2B 管理系統");
            
            String emailContent = String.format(
                "親愛的 %s，\n\n" +
                "歡迎加入 B2B 管理系統！您的帳戶已成功建立。\n\n" +
                "您現在可以使用您的電子郵件地址和密碼登入系統。\n\n" +
                "登入網址：%s/login\n\n" +
                "如果您有任何問題，請隨時聯繫我們。\n\n" +
                "謝謝！\n" +
                "B2B 管理系統團隊",
                userName,
                frontendUrl
            );
            
            message.setText(emailContent);
            
            mailSender.send(message);
            logger.info("歡迎郵件已發送至: {}", toEmail);
            
        } catch (Exception e) {
            logger.error("發送歡迎郵件失敗，收件人: {}", toEmail, e);
            // 不拋出異常，因為註冊應該成功即使郵件發送失敗
            logger.warn("用戶註冊成功但歡迎郵件發送失敗");
        }
    }
}