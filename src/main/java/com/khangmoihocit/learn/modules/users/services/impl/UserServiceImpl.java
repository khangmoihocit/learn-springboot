package com.khangmoihocit.learn.modules.users.services.impl;

import com.khangmoihocit.learn.Resources.ApiResource;
import com.khangmoihocit.learn.Resources.ErrorResource;
import com.khangmoihocit.learn.modules.users.entities.User;
import com.khangmoihocit.learn.modules.users.repositories.UserRepository;
import com.khangmoihocit.learn.services.BaseService;
import com.khangmoihocit.learn.modules.users.requests.LoginRequest;
import com.khangmoihocit.learn.modules.users.resources.LoginResource;
import com.khangmoihocit.learn.modules.users.resources.UserResource;
import com.khangmoihocit.learn.modules.users.services.interfaces.UserService;
import com.khangmoihocit.learn.services.JwtService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl extends BaseService implements UserService {

    UserRepository userRepository;
    JwtService jwtService;
    PasswordEncoder passwordEncoder;

    @Value("${jwt.defaultExpiration}")
    @NonFinal
    long defaultExpiration;

    @Override
    public Object authenticate(LoginRequest loginRequest) {
        try {
            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new BadCredentialsException("email hoặc mật khẩu không chính xác."));
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                throw new BadCredentialsException("email hoặc mật khẩu không chính xác.");
            }

            String token = jwtService.generateToken(user.getId(), user.getEmail(), defaultExpiration);
            String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getEmail());
            UserResource userResource = UserResource.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .name(user.getName())
                    .build();

            return LoginResource.builder()
                    .token(token)
                    .refreshToken(refreshToken)
                    .user(userResource)
                    .build();

        } catch (BadCredentialsException ex) {
            return ApiResource.error("AUTH_ERROR", ex.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }


}
