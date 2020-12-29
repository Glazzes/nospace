package com.nospace.controller;

import com.nospace.model.NewAccountRequest;
import com.nospace.repository.FolderRepository;
import com.nospace.entities.Folder;
import com.nospace.entities.User;
import com.nospace.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/auth")
public class TestController {

    @Autowired private FolderRepository folderRepository;
    @Autowired private UserService userService;

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
