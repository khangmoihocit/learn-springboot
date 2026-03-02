package com.khangmoihocit.learn.cronjob;

import com.khangmoihocit.learn.modules.users.entities.RefreshToken;
import com.khangmoihocit.learn.modules.users.repositories.BlacklistedTokenRepository;
import com.khangmoihocit.learn.modules.users.repositories.RefreshTokenRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j(topic = "RefreshTokenClean")
public class RefreshTokenClean {
    RefreshTokenRepository refreshTokenRepository;

    @Transactional
    @Scheduled(cron = "0 0 0 * * ?")//00:00 every day
    public void cleanUpExpiryToken(){
        LocalDateTime currentDateTime = LocalDateTime.now();
        int deleteCount = refreshTokenRepository.deleteByExpiryDateBefore(currentDateTime);
        log.info("Đã xóa " + deleteCount + " token");
    }

}
