package com.nospace.controller;

import com.nospace.Repository.FolderRepository;
import com.nospace.entities.Folder;
import com.nospace.entities.User;
import com.nospace.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins="http://localhost:3000", maxAge=3600)
public class TestController {

    @Autowired private FolderRepository folderRepository;
    @Autowired private UserService userService;

    @GetMapping("/folder-size")
    public long folderSize() throws IOException {
        Path folder = Paths.get("/home/glaze/go/4847af553ec-root/");
        return Files.walk(folder).mapToLong(f -> f.toFile().length()).sum();
    }

    @GetMapping("/folders")
    public List<Folder> returnFolders(
        @RequestParam(name = "folder", required = false) String folderName,
        Principal principal
    ){
        final String finalFolderName = Optional.ofNullable(folderName)
            .orElseGet(() -> {
                Optional<User> user =  userService.findByUsername(principal.getName());
                return user.get().getId()+"-root/";
            });
        return folderRepository.findByNameMatchesRegex(finalFolderName);
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
