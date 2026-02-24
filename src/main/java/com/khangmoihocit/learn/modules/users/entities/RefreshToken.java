package com.khangmoihocit.learn.modules.users.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, name = "user_id")
    Long userId;

    @Column(name = "refresh_token", nullable = false, unique = true, columnDefinition = "TEXT")
    String refreshToken;

    @Column(name = "expiry_date", nullable = false)
    LocalDateTime expiryDate;
    @Column(name = "create_at")
    LocalDateTime createAt;
    @Column(name = "update_at")
    LocalDateTime updateAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    User user;

    @PrePersist
    protected void onCreated(){
        createAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdated(){
        updateAt = LocalDateTime.now();
    }
}
