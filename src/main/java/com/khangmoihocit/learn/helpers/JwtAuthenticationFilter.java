package com.khangmoihocit.learn.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.khangmoihocit.learn.modules.users.services.impl.UserDetailsServiceImpl;
import com.khangmoihocit.learn.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.internal.util.StringHelper;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j(topic = "JWT FILTER")
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    JwtService jwtService;
    UserDetailsServiceImpl userDetailsService;

//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
//        String path = String.valueOf(request.getRequestURL());
//
//        return path.startsWith("/api/v1/auth");
//    }

    //xac thuc token
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt; //token string
        final String userId;

        //nếu request không có token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.info("request Không có token");
            filterChain.doFilter(request, response); //tiếp tục check ở filter chain
            return;
        }

        //nếu request có token -> kiểm tra token có hợp lệ không,
        //nếu hợp lệ trích xuất thông tin từ token rồi extract sang userdetail rồi lưu vào security context
        try {
            jwt = authHeader.substring(7);
            if (jwtService.isTokenExpired(jwt)) {
                sendErrorResponse(response, request, HttpServletResponse.SC_UNAUTHORIZED,
                        "Xác thực không thành công",
                        "token đã hết hạn");
                return;
            }

            if (!jwtService.isTokenFormatValid(jwt)) {
                sendErrorResponse(response, request, HttpServletResponse.SC_UNAUTHORIZED,
                        "Xác thực không thành công",
                        "token không đúng định dạng");
                return;
            }

            if (!jwtService.isSignatureValid(jwt)) {
                sendErrorResponse(response, request, HttpServletResponse.SC_UNAUTHORIZED,
                        "Xác thực không thành công",
                        "chữ ký token không hợp lệ");
                return;
            }

            if (!jwtService.isIssuerToken(jwt)) {
                sendErrorResponse(response, request, HttpServletResponse.SC_UNAUTHORIZED,
                        "Xác thực không thành công",
                        "token có nguồn gốc không hợp lệ");
                return;
            }

            if (!jwtService.isTokenFormatValid(jwt)) {
                sendErrorResponse(response, request, HttpServletResponse.SC_UNAUTHORIZED,
                        "Xác thực không thành công",
                        "token không đúng định dạng");
                return;
            }

            if(jwtService.isBlacklistedToken(jwt)){
                sendErrorResponse(response, request, HttpServletResponse.SC_UNAUTHORIZED,
                        "Xác thực không thành công",
                        "token bị khóa.");
                return;
            }
            userId = jwtService.extractUsername(jwt); //validate token

            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(userId);
                final String emailFromToken = jwtService.getEmailFromJwt(jwt);
                if (!emailFromToken.equals(userDetails.getUsername())) {
                    sendErrorResponse(response, request, HttpServletResponse.SC_UNAUTHORIZED,
                            "Xác thực không thành công",
                            "User token không chính xác");
                    return;
                }

                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                //luu vao security context de sau nay lay ra xu ly
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                log.info("Xác thực tài khoản thành công (token hợp lệ): " + userDetails.getUsername());

            }
            filterChain.doFilter(request, response); //sau đó tiếp tục kiểm tra ở security config
        } catch (ServletException | IOException ex) {
            sendErrorResponse(response, request,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Network Error!",
                    ex.getMessage());
        }
    }

    private void sendErrorResponse(
            @NotNull HttpServletResponse response,
            @NotNull HttpServletRequest request,
            int statusCode, String error, String message) throws IOException {

        response.setStatus(statusCode);
        response.setContentType("application/json;charset=UTF-8");

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("Timestamp", System.currentTimeMillis());
        errorResponse.put("status", statusCode);
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        errorResponse.put("path", request.getRequestURL());

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);

        response.getWriter().write(jsonResponse);
    }
}
