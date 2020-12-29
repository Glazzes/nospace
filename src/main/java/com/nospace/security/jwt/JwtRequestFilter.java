package com.nospace.security.jwt;

import com.google.common.collect.ImmutableList;
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
import java.util.List;

@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter{

    private final JwtProvider provider;
    public JwtRequestFilter(JwtProvider provider) {
        this.provider = provider;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        List<String> freeUrls = ImmutableList.of("/api/login", "/api/account/register");
        return freeUrls.contains(request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        String authorizationToken = provider.getCookieValue(cookies, "Authorization");
        String refreshToken = provider.getCookieValue(cookies, "Refresh");

        if(!provider.validateToken(refreshToken)){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
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
