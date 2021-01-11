package com.nospace.security.jwt;

import com.nospace.entities.User;
import com.nospace.security.CookieType;
import com.nospace.security.SecurityCipher;
import com.nospace.security.UserDetailsImpl;
import com.nospace.services.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtProvider {

    private final JwtProperties properties;
    private final UserService userService;
    public JwtProvider(JwtProperties properties, UserService userService) {
        this.properties = properties;
        this.userService = userService;
    }

    public Cookie generateCookieToken(
        UserDetailsImpl userDetails, CookieType type
    ){
        String token = Jwts.builder()
            .setId(UUID.randomUUID().toString())
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date())
            .claim("authorities", userDetails.getAuthorities())
            .signWith(Keys.hmacShaKeyFor(properties.getSecretKey().getBytes(StandardCharsets.US_ASCII)),
                SignatureAlgorithm.HS256)
            .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusDays(properties.getExpirationInDays())))
            .compact();

        String encryptedToken = SecurityCipher.encryptCookieJwtToken(token);

        Cookie cookie = new Cookie(type.getCookieName(), encryptedToken);
        cookie.setMaxAge(type.getExpirationTimeInSeconds());
        cookie.setHttpOnly(true);
        cookie.setPath("/api");

        return cookie;
    }

    public String getCookieValue(Cookie[] cookies, String cookieName){
        return Arrays.stream(cookies)
                   .filter(c-> c.getName().equals(cookieName))
                   .map(Cookie::getValue)
                   .collect(Collectors.joining());
    }

    public boolean validateToken(String encryptedToken){
        try{
            String decryptedToken = SecurityCipher.decryptCookieJWtToken(encryptedToken);

            Jwts.parserBuilder()
                .setSigningKey(
                    Keys.hmacShaKeyFor(properties.getSecretKey().getBytes(StandardCharsets.US_ASCII)))
                .build()
                .parseClaimsJws(decryptedToken);

            return true;
        }catch(MalformedJwtException e){
            log.info("The provided token was not a valid json web token");
            e.printStackTrace();
        }catch (Exception e){
            log.info("Error decrypted the json web token by SecurityCipher");
        }

        return false;
    }

    public UserDetailsImpl getUserFromToken(String token){
        String decryptedToken = SecurityCipher.decryptCookieJWtToken(token);

        Claims body = Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(properties.getSecretKey().getBytes()))
            .build()
            .parseClaimsJws(decryptedToken)
            .getBody();

        Optional<User> user = userService.findByUsername(body.getSubject());
        return user.map(UserDetailsImpl::new).get();
    }

    public LocalDateTime getExpirationFromToken(String token){
        Claims claims = Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(properties.getSecretKey().getBytes()))
                            .build().parseClaimsJws(token).getBody();

        return LocalDateTime.ofInstant(claims.getExpiration().toInstant(), ZoneId.systemDefault());
    }

}
