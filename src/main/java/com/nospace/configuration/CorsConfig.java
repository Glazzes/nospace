package com.nospace.configuration;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/login").allowedOrigins("http://localhost:3000").allowCredentials(true);
	registry.addMapping("/account/**").allowedOrigins("http://localhost:3000").allowCredentials(true);
	registry.addMapping("/file/**").allowedOrigins("http://localhost:3000").allowCredentials(true);
        registry.addMapping("/**").allowedOrigins("*");
    }

}
