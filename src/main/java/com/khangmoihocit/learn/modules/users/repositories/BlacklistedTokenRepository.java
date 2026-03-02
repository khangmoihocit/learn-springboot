package com.khangmoihocit.learn.modules.users.repositories;

import com.khangmoihocit.learn.modules.users.entities.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Long> {
    boolean existsByToken(String token);
    int deleteByExpiryDateBefore(LocalDateTime currentDateTime);
}
