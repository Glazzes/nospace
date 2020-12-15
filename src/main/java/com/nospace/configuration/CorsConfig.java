package com.nospace.configuration;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        final String FRONTEND = "http://localhost:3000";

        registry.addMapping("/login").allowedOrigins(FRONTEND).allowCredentials(true);
        registry.addMapping("/content/**").allowedOrigins(FRONTEND).allowCredentials(true);
        registry.addMapping("/account/**").allowedOrigins(FRONTEND).allowCredentials(true);
	    registry.addMapping("/files/**").allowedOrigins(FRONTEND).allowCredentials(true);
	    registry.addMapping("/auth/**").allowedOrigins(FRONTEND).allowCredentials(true);
	    registry.addMapping("*").allowedOrigins("*");
    }

}