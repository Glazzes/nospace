package com.nospace.security;

import com.nospace.security.jwt.JwtAuthenticationFilter;
import com.nospace.security.jwt.JwtProvider;
import com.nospace.security.jwt.JwtRequestFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;

import java.time.Duration;
import java.util.List;

@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final JwtProvider provider;
    public WebSecurityConfiguration(UserDetailsService userDetailsService, JwtProvider provider){
        this.userDetailsService = userDetailsService;
        this.provider = provider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .cors().configurationSource(request -> {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowCredentials(true);
            configuration.setAllowedOrigins(List.of("http://localhost:3000", "127.0.0.1:80"));
            configuration.setAllowedMethods(List.of("GET", "POST", "PATCH", "DELETE", "OPTIONS"));
            configuration.setMaxAge(Duration.ofMinutes(60));
            configuration.setAllowedHeaders(List.of("*"));
            return configuration;
        })
            .and()
            .authorizeRequests()
            .antMatchers(HttpMethod.POST, "/api/account/register").permitAll()
            .antMatchers(HttpMethod.POST, "/api/login").permitAll()
            .anyRequest()
            .authenticated();

        http
            .addFilter(new JwtAuthenticationFilter(authenticationManager(), provider))
            .addFilterAfter(new JwtRequestFilter(provider), JwtAuthenticationFilter.class)
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/api/account/profile-picture/**")
            .antMatchers(HttpMethod.GET, "/api/files/{id}/download")
            .antMatchers(HttpMethod.GET, "/imgs/**")
            .antMatchers(HttpMethod.GET, "/api/content/{id}/download");
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(10);
    }
}
