package com.nospace.controller;

import com.nospace.entities.User;
import com.nospace.security.UserDetailsImpl;
import com.nospace.security.jwt.JwtProvider;
import com.nospace.services.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/some")
public class SomeController {

    private final JwtProvider provider;
    private final UserService userService;
    public SomeController(JwtProvider provider, UserService userService) {
        this.provider = provider;
        this.userService = userService;
    }

    @GetMapping
    public String home(){
        return "You are authenticated!";
    }

    @GetMapping("/testing")
    public String getUser(Principal principal){
        return principal.getName();
    }

}