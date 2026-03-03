package com.khangmoihocit.learn.modules.users.services.impl;

import com.khangmoihocit.learn.Resources.ApiResource;
import com.khangmoihocit.learn.Resources.ErrorResource;
import com.khangmoihocit.learn.Resources.MessageResource;
import com.khangmoihocit.learn.modules.users.entities.BlacklistedToken;
import com.khangmoihocit.learn.modules.users.repositories.BlacklistedTokenRepository;
import com.khangmoihocit.learn.modules.users.requests.BlacklistTokenRequest;
import com.khangmoihocit.learn.modules.users.services.interfaces.BlacklistedTokenService;
import com.khangmoihocit.learn.services.JwtService;
import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
@Slf4j(topic = "BLACKLIST TOKEN")
public class BlacklistedTokenServiceImpl implements BlacklistedTokenService {
    BlacklistedTokenRepository blacklistedTokenRepository;
    JwtService jwtService;

    @Override
    public Object create(BlacklistTokenRequest request){
        try{
            if(blacklistedTokenRepository.existsByToken(request.getToken())){
                return ApiResource.error("BLACK_LIST_TOKEN", "token đã tồn tại trong black list", HttpStatus.NOT_FOUND);
            }

            Claims claims = jwtService.extractAllClaims(request.getToken());
            Long userId = Long.valueOf(claims.getSubject());
            Date expiryDate = claims.getExpiration();

            BlacklistedToken blacklistedToken = BlacklistedToken.builder()
                    .token(request.getToken())
                    .userId(userId)
                    .expiryDate(expiryDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                    .build();

            blacklistedTokenRepository.save(blacklistedToken);
            log.info("Thêm token vào blacklist thành công.");
            return new MessageResource("Thêm token vào blacklist thành công.");
        }catch (Exception ex){
            return new MessageResource("Network Error! " + ex.getMessage());
        }
    }
}
