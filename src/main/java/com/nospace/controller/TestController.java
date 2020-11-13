package com.nospace.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins="http://localhost:3000", maxAge=3600)
public class TestController {

    @GetMapping(produces = "application/json")
    public String injectCookie(HttpServletResponse response){
        Cookie cookie = new Cookie("df", "sdfsdfs");
        cookie.setMaxAge(3600);
        cookie.setHttpOnly(true);

        response.addCookie(cookie);
        return "Hello world";
    }

    @GetMapping("/cookie")
    public void printCookie(
        @CookieValue( name = "Something") String cookie,
        @CookieValue( name = "NoHttpOnly") String secondCookie
    ){
        System.out.println(cookie);
        System.out.println(secondCookie);
    }

}
