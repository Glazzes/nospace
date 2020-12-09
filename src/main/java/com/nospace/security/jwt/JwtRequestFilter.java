package com.nospace.security.jwt;

import com.nospace.security.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter{

    private final JwtProvider provider;
    public JwtRequestFilter(JwtProvider provider) {
        this.provider = provider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String PATH = request.getRequestURI();
        if(PATH.equals("/api/account/register")){
            filterChain.doFilter(request, response);
            return;
        }

        Cookie[] cookies = request.getCookies();
        String authorizationToken = provider.getCookieValue(cookies, "Authorization");
        String refreshToken = provider.getCookieValue(cookies, "Refresh");

        if(!provider.validateToken(refreshToken)){
            filterChain.doFilter(request, response);
            return;
        }

        UserDetailsImpl user = provider.getUserFromToken(refreshToken);
        if(!provider.validateToken(authorizationToken) && provider.validateToken(refreshToken)){
            Cookie authorizationTokenCookie = provider.generateCookieToken(user, CookieType.AUTHORIZATION_TOKEN);
            response.addCookie(authorizationTokenCookie);
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
            user.getUsername(), null, user.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}
