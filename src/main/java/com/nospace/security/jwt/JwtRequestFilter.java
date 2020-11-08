/*
package com.nospace.security.jwt;

import com.google.common.base.Strings;
import com.nospace.security.UserDetailsImpl;
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

public class JwtRequestFilter extends OncePerRequestFilter{

    private final JwtProvider provider;
    public JwtRequestFilter(JwtProvider provider) {
        this.provider = provider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        String authorizationToken = provider.getCookieValue(cookies, "Authorization");
        String refreshToken = provider.getCookieValue(cookies, "Refresh");

        if(Strings.isNullOrEmpty(authorizationToken) && Strings.isNullOrEmpty(refreshToken)){
            filterChain.doFilter(request, response);
            return;
        }

        UserDetailsImpl user = provider.getUserFromToken(refreshToken);
        if(!Strings.isNullOrEmpty(refreshToken) && Strings.isNullOrEmpty(authorizationToken)){
            Cookie authorizationCookie = provider.generateCookieToken(user, CookieType.AUTHORIZATION_TOKEN);
            response.addCookie(authorizationCookie);
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
            user.getUsername(), user.getPassword(), user.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}
 */
