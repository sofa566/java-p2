package com.b2b.service;

import com.b2b.entity.PasswordResetToken;
import com.b2b.entity.User;
import com.b2b.repository.PasswordResetTokenRepository;
import com.b2b.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordResetTokenRepository tokenRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private EmailService emailService;
    
    private static final int TOKEN_EXPIRY_HOURS = 24;
    private static final int MAX_TOKENS_PER_HOUR = 3;
    
    @Transactional
    public String createPasswordResetToken(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isEmpty()) {
            // 為了安全起見，即使用戶不存在也返回成功訊息
            return "如果該電子郵件地址存在於我們的系統中，您將收到密碼重置連結。";
        }
        
        User user = userOpt.get();
        
        // 檢查是否在過去一小時內已發送了太多重置郵件
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        Long recentTokens = tokenRepository.countRecentTokensForUser(user, oneHourAgo);
        
        if (recentTokens >= MAX_TOKENS_PER_HOUR) {
            return "您已請求了太多密碼重置。請稍後再試。";
        }
        
        // 刪除該用戶的舊令牌
        tokenRepository.deleteByUser(user);
        
        // 創建新令牌
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(TOKEN_EXPIRY_HOURS);
        
        PasswordResetToken resetToken = new PasswordResetToken(token, user, expiryDate);
        tokenRepository.save(resetToken);
        
        // 發送密碼重置郵件
        try {
            emailService.sendPasswordResetEmail(user.getEmail(), token);
        } catch (Exception e) {
            // 記錄錯誤但不影響用戶體驗
            System.err.println("發送郵件失敗: " + e.getMessage());
        }
        
        return "如果該電子郵件地址存在於我們的系統中，您將收到密碼重置連結。";
    }
    
    @Transactional
    public String resetPassword(String token, String newPassword) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByTokenAndUsedFalse(token);
        
        if (tokenOpt.isEmpty()) {
            return "無效的重置令牌。";
        }
        
        PasswordResetToken resetToken = tokenOpt.get();
        
        if (resetToken.isExpired()) {
            return "重置令牌已過期。請重新申請密碼重置。";
        }
        
        // 更新用戶密碼
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        // 標記令牌為已使用
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
        
        return "密碼已成功重置。";
    }
    
    public String validateToken(String token) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByTokenAndUsedFalse(token);
        
        if (tokenOpt.isEmpty()) {
            return "無效的重置令牌。";
        }
        
        PasswordResetToken resetToken = tokenOpt.get();
        
        if (resetToken.isExpired()) {
            return "重置令牌已過期。請重新申請密碼重置。";
        }
        
        return "令牌有效。";
    }
    
    @Transactional
    public void cleanupExpiredTokens() {
        tokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }
    
}