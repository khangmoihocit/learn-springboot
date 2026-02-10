package com.khangmoihocit.learn.modules.users.controllers;

import com.khangmoihocit.learn.Resources.ErrorResource;
import com.khangmoihocit.learn.Resources.MessageResource;
import com.khangmoihocit.learn.modules.users.entities.RefreshToken;
import com.khangmoihocit.learn.modules.users.requests.BlacklistTokenRequest;
import com.khangmoihocit.learn.modules.users.requests.LoginRequest;
import com.khangmoihocit.learn.modules.users.requests.RefreshTokenRequest;
import com.khangmoihocit.learn.modules.users.resources.LoginResource;
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

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Validated
@RequestMapping("${api.prefix}/auth")
public class AuthController {
    UserService userService;
    BlacklistedTokenService blacklistedTokenService;
    JwtService jwtService;

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
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String bearerToken){
        String refreshToken = bearerToken.substring(7);

        if(!jwtService.isRefreshTokenValid(refreshToken)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResource("Refresh token không hợp lệ hoặc đã hết hạn."));
        }

        return ResponseEntity.ok(refreshToken);
    }
}
