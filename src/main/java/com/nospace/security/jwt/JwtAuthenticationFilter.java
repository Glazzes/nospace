package com.nospace.security.jwt;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nospace.security.LoginRequest;
import com.nospace.security.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager manager;
    private final JwtProvider provider;
    public JwtAuthenticationFilter(AuthenticationManager manager, JwtProvider provider){
        this.manager = manager;
        this.provider = provider;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            LoginRequest loginRequest = new ObjectMapper().readValue(
                request.getInputStream(), LoginRequest.class
            );

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(), loginRequest.getPassword()
            );

            return manager.authenticate(authentication);
        } catch (IOException e) {
            log.info("Error casting the user request to a login request");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        UserDetailsImpl user = (UserDetailsImpl) authResult.getPrincipal();
        Cookie authorizationTokenCookie = provider.generateCookieToken(user, CookieType.AUTHORIZATION_TOKEN);
        Cookie refreshTokenCookie = provider.generateCookieToken(user, CookieType.REFRESH_TOKEN);

        response.addCookie(authorizationTokenCookie);
        response.addCookie(refreshTokenCookie);
    }
}
