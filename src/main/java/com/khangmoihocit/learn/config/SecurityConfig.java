package com.khangmoihocit.learn.config;

import com.khangmoihocit.learn.helpers.JwtAuthenticationFilter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SecurityConfig {

    JwtAuthenticationFilter jwtAuthenticationFilter;
    final String[] ENDPOIN_POST = {"/api/v1/auth/login", "/api/v1/auth/refresh-token", "/api/v1/auth/blacklist-token"};

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)

                //không có token hoặc token hợp lệ sẽ đều chạy tiếp vào đây - KIỂM TRA request đó có quyền không sử dụng không
                .authorizeHttpRequests(auth -> auth
                        //1. Routers auth - no jwt
//                        .requestMatchers(HttpMethod.GET, "/api/v1/users/my-profile").permitAll()

                        //2. Routers PUBLIC
                        .requestMatchers(HttpMethod.POST, ENDPOIN_POST).permitAll()

                        .anyRequest().authenticated()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //chạy filter này trước sau đó mới xét permiall ở trên hay không
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        ;

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(10);}
}

