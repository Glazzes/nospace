package com.nospace.security.jwt;

import com.nospace.entities.User;
import com.nospace.security.UserDetailsImpl;
import com.nospace.services.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

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
            .setId(userDetails.getId())
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date())
            .claim("authorities", userDetails.getAuthorities())
            .signWith(Keys.hmacShaKeyFor(properties.getSecretKey().getBytes()), SignatureAlgorithm.HS512)
            .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusDays(properties.getExpirationInDays())))
            .compact();

        Cookie cookie = new Cookie(type.getCookieName(), token);
        cookie.setMaxAge(type.getExpirationTimeInSeconds());
        // cookie.setHttpOnly(true);

        return cookie;
    }

    public LocalDateTime getExpirationFromToken(String token){
        Claims claims = Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(properties.getSecretKey().getBytes()))
            .build().parseClaimsJws(token).getBody();

        return LocalDateTime.ofInstant(claims.getExpiration().toInstant(), ZoneId.systemDefault());
    }

    public UserDetailsImpl getUserFromToken(String token){
        Claims body = Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(properties.getSecretKey().getBytes()))
            .build()
            .parseClaimsJws(token)
            .getBody();

        Optional<User> user = userService.findByUsername(body.getSubject());
        return user.map(UserDetailsImpl::new).get();
    }

    public String getCookieValue(Cookie[] cookies, String cookieName){
        return Arrays.stream(cookies)
            .filter(c-> c.getName().equals(cookieName))
            .map(Cookie::getValue)
            .collect(Collectors.joining());
    }

}
