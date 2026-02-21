package com.khangmoihocit.learn.services;

import com.khangmoihocit.learn.config.JwtConfig;
import com.khangmoihocit.learn.modules.users.entities.RefreshToken;
import com.khangmoihocit.learn.modules.users.repositories.BlacklistedTokenRepository;
import com.khangmoihocit.learn.modules.users.repositories.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.PrivateJwk;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "JWT SERVICE")
public class JwtService {

    private final JwtConfig jwtConfig;
    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtConfig.getSecretKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Long userId, String email) {
        Date now = new Date();
        log.info(String.valueOf(jwtConfig.getExpirationTime()));
        Date expiryDate = new Date(now.getTime() + jwtConfig.getExpirationTime());
        log.info(expiryDate.toString());
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("email", email)
                .issuer(jwtConfig.getIssuer())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSignInKey(), Jwts.SIG.HS512)
                .compact();
    }

    public String generateRefreshToken(Long userId, String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtConfig.getExpirationTimeRefreshToken());

        String strRefreshToken = Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("email", email)
                .issuer(jwtConfig.getIssuer())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSignInKey(), Jwts.SIG.HS512)
                .compact();

        //đaảm bảo mỗi user sẽ chỉ có 1 refreshtoken đc lưu db
        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByUserId(userId);
        if(optionalRefreshToken.isPresent()){ //không có bản ghi nào
            RefreshToken refreshToken = optionalRefreshToken.get();
            refreshToken.setRefreshToken(strRefreshToken);
            refreshToken.setExpiryDate(expiryDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
            refreshTokenRepository.save(refreshToken);
        }else{
            RefreshToken refreshToken = RefreshToken.builder()
                    .refreshToken(strRefreshToken)
                    .userId(userId)
                    .expiryDate(expiryDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                    .build();
            refreshTokenRepository.save(refreshToken);
        }

        return strRefreshToken;
    }

    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    public String getEmailFromJwt(String token){
        Claims claims = extractAllClaims(token);
        return claims.get("email", String.class);
    }

    /*
    1. token có đúng định dạng không
    2. chữ ký của token có đúng không
    3. kiểm tra xem token có hết hạn hay chuưa
    4.  user_id của token có khớp với userdetail không
    5. kiểm tra xem token có trong blacklist không
    6. kiểm tra quyền
    */

    public boolean isBlacklistedToken(String token){
        return blacklistedTokenRepository.existsByToken(token);
    }

    public boolean isTokenFormatValid(String token){
        try{
            String[] tokenParts = token.split("\\.");
            return tokenParts.length == 3;
        }catch (Exception e){
            return false;
        }
    }

    public boolean isSignatureValid(String token){
        try{
            Jwts.parser().verifyWith(getSignInKey()).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            final Date expiration = extractClaims(token, Claims::getExpiration);
            if (expiration == null) {
                return true;
            }
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            return true;
        }
    }

    public boolean isIssuerToken(String token){
        String tokenIssuer = extractClaims(token, Claims::getIssuer);
        return tokenIssuer.equals(jwtConfig.getIssuer());
    }

    //true: token con han
    //false: token het han han
    public boolean isRefreshTokenValid(String token){
        try{
            Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token);
            RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(token)
                    .orElseThrow(()-> new RuntimeException("refreshtoken không hợp lệ"));
            final Date expire = extractAllClaims(refreshToken.getRefreshToken()).getExpiration();

            return expire.after(new Date());
        }catch (Exception ex){
            return false;
        }
    }

    // Lấy theo nhiều claim khác nhau
    public <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Giải mã token và lấy tất cả claims
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}