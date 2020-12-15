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
import org.springframework.web.bind.annotation.CrossOrigin;

@EnableWebSecurity
@Configuration
@CrossOrigin("*")
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
        http.cors().and().csrf().disable()
            .authorizeRequests()
            .antMatchers(HttpMethod.POST, "/account/register").permitAll()
            .antMatchers(HttpMethod.POST, "/login").permitAll()
            .antMatchers("/auth/**").permitAll()
            .antMatchers("/file/**").permitAll()
            .antMatchers(HttpMethod.GET, "/imgs/**").permitAll()
            .anyRequest().authenticated();

        http
            .addFilter(new JwtAuthenticationFilter(authenticationManager(), provider))
            .addFilterAfter(new JwtRequestFilter(provider), JwtAuthenticationFilter.class)
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/account/profile-picture/**")
            .antMatchers(HttpMethod.GET, "/files/{id}/download")
            .antMatchers(HttpMethod.GET, "/content/{id}/download");
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(10);
    }
}
