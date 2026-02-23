package com.khangmoihocit.learn.modules.users.controllers;

import com.khangmoihocit.learn.Resources.ErrorResource;
import com.khangmoihocit.learn.Resources.MessageResource;
import com.khangmoihocit.learn.modules.users.entities.RefreshToken;
import com.khangmoihocit.learn.modules.users.repositories.RefreshTokenRepository;
import com.khangmoihocit.learn.modules.users.requests.BlacklistTokenRequest;
import com.khangmoihocit.learn.modules.users.requests.LoginRequest;
import com.khangmoihocit.learn.modules.users.requests.RefreshTokenRequest;
import com.khangmoihocit.learn.modules.users.resources.LoginResource;
import com.khangmoihocit.learn.modules.users.resources.RefreshTokenResource;
import com.khangmoihocit.learn.modules.users.resources.TokenResource;
import com.khangmoihocit.learn.modules.users.services.interfaces.BlacklistedTokenService;
import com.khangmoihocit.learn.modules.users.services.interfaces.UserService;
import com.khangmoihocit.learn.services.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Validated
@RequestMapping("${api.prefix}/auth")
public class AuthController {
    UserService userService;
    BlacklistedTokenService blacklistedTokenService;
    JwtService jwtService;
    RefreshTokenRepository refreshTokenRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        Object result = userService.authenticate(request);

        if(result instanceof LoginResource loginResource){
            return ResponseEntity.ok(loginResource);
        }

        if(result instanceof ErrorResource errorResource){
            return ResponseEntity.unprocessableEntity().body(errorResource);
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Network error");
    }

    @PostMapping("/blacklist-token")
    public ResponseEntity<?> addTokenToBlacklist(@Valid @RequestBody BlacklistTokenRequest request) {
        try{
            Object result = blacklistedTokenService.create(request);
            return ResponseEntity.ok(result);
        }catch (Exception ex){
                return ResponseEntity.internalServerError().body(new MessageResource("Network Error!"));
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String bearerToken){
        try{
            String token = bearerToken.substring(7);
            Object result = blacklistedTokenService.create(new BlacklistTokenRequest(token));
            return ResponseEntity.ok(result);
        }catch (Exception ex){
            return ResponseEntity.internalServerError().body(new MessageResource("Network Error!"));
        }
    }



    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest request){
        String refreshToken = request.getToken();

        try{
            if(!jwtService.isRefreshTokenValid(refreshToken)){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new MessageResource("Refresh token không hợp lệ hoặc đã hết hạn."));
            }
        }catch (RuntimeException ex){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResource(ex.getMessage()));
        }

        Optional<RefreshToken> dbRefreshTokenOptional = refreshTokenRepository.findByRefreshToken(refreshToken);
        if(dbRefreshTokenOptional.isPresent()){
            RefreshToken dbRefreshToken = dbRefreshTokenOptional.get();
            Long userId = dbRefreshToken.getUserId();
            String email = dbRefreshToken.getUser().getEmail();

            String newToken = jwtService.generateToken(userId, email);
            String newRefreshToken = jwtService.generateRefreshToken(userId, email);

            return ResponseEntity.ok(RefreshTokenResource.builder()
                    .token(newToken)
                    .refreshToken(newRefreshToken)
                    .build());
        }
        return ResponseEntity.internalServerError().body(new MessageResource("Network Error!"));
    }
}
