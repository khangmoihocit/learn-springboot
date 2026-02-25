package com.khangmoihocit.learn.modules.users.controllers;

import com.khangmoihocit.learn.Resources.ApiResource;
import com.khangmoihocit.learn.Resources.SuccessResource;
import com.khangmoihocit.learn.modules.users.entities.User;
import com.khangmoihocit.learn.modules.users.repositories.UserRepository;
import com.khangmoihocit.learn.modules.users.resources.UserResource;
import com.sun.net.httpserver.Authenticator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j(topic = "USER CONTROLLER")
public class UserController {

    UserRepository userRepository;

    @GetMapping("/my-profile")
    ResponseEntity<?> me(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info(email);

        User user = userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User không tồn tại"));
        UserResource userResource = UserResource.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build();

        ApiResource<UserResource> response = ApiResource.ok(userResource, "SUCCESS");

        return ResponseEntity.ok(response);
    }
}
