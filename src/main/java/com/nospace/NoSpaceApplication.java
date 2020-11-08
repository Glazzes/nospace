package com.nospace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableAsync
@EnableSwagger2
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class NoSpaceApplication{

	public static void main(String[] args) {
		SpringApplication.run(NoSpaceApplication.class, args);
	}
}