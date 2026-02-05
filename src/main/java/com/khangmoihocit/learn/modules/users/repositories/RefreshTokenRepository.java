package com.khangmoihocit.learn.modules.users.repositories;

import com.khangmoihocit.learn.modules.users.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByRefreshToken(String tokenToken);
    Optional<RefreshToken> findByUserIdAndExpiryDateAfter(Long userId, LocalDateTime now);
    Optional<RefreshToken> findByUserId(Long userId);

}
