package com.nospace.controller;

import com.nospace.model.NewAccountRequest;
import com.nospace.repository.FolderRepository;
import com.nospace.entities.Folder;
import com.nospace.entities.User;
import com.nospace.security.jwt.JwtProperties;
import com.nospace.services.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/auth")
public class TestController {

    @Autowired private FolderRepository folderRepository;
    @Autowired private UserService userService;
    @Autowired private JwtProperties properties;

    @GetMapping("/testing")
    public String testingJwt(){
        try{
            String token = "eyJhbGciOiJIUzUxMiJ9.eyJqdGkiOiIzOTY5MTZlNjI0OSIsInN1YiI6ImdsYXplIiwiaWF0IjoxNjEwMTQ5MTA0LCJhdXRob3JpdGllcyI6W3siYXV0aG9yaXR5IjoiUk9MRV9VU0VSIn0seyJhdXRob3JpdHkiOiJGSUxFX1VQTE9BRCJ9LHsiYXV0aG9yaXR5IjoiRklMRV9ERUxFVEUifSx7ImF1dGhvcml0eSI6IkZJTEVfUkVQT1JUIn1dLCJleHAiOjE2MTAyNTQ4MDB9._x1BbMeyy4E4q0XkpF0JELLQI7nVZeQchaqSFOZdgHy_UgdPixKL_BAtnYjpckSEFOeSBPxsb9nJDchzxjEqMA";
            byte[] tokenBytes = token.getBytes(StandardCharsets.UTF_8);
            String newString = new String(tokenBytes, StandardCharsets.UTF_8);

            Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(properties.getSecretKey().getBytes()))
                .build()
                .parseClaimsJws(newString);

            System.out.println(newString.length());
            return "Token valido";
        }catch (MalformedJwtException e){
            e.printStackTrace();
        }

        return "Token invalido";
    }

    @GetMapping("/folder-size")
    public long folderSize() throws IOException {
        Path folder = Paths.get("/home/glaze/go/4847af553ec-root/");
        return Files.walk(folder).mapToLong(f -> f.toFile().length()).sum();
    }

    @PostMapping(path= "/test", produces = "application/json")
    public ResponseEntity<?> testing(@Valid @RequestBody NewAccountRequest request){
        return ResponseEntity.ok().body(request);
    }

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
