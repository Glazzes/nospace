package com.nospace.controller;

import com.nospace.entities.User;
import com.nospace.security.UserDetailsImpl;
import com.nospace.security.jwt.JwtProvider;
import com.nospace.services.UserService;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/some")
@CrossOrigin("*")
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
    public User getUser(@CookieValue(name = "Authorization") String authorizationToken){
        UserDetailsImpl user = provider.getUserFromToken(authorizationToken);
        User found = userService.findByUsername(user.getUsername()).get();
        return found;
    }

}