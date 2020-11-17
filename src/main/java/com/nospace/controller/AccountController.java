package com.nospace.controller;

import com.nospace.entities.User;
import com.nospace.model.NewAccountRequest;
import com.nospace.services.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/account")
@CrossOrigin("*")
public class AccountController {

    private final AccountService accountService;
    private final VerificationTokenService verificationTokenService;
    private final FileServiceImpl fileService;
    private final UserService userService;

    public AccountController(
        AccountService accountService,
        VerificationTokenService verificationTokenService,
        FileServiceImpl fileService,
        UserService userService
    ) {
        this.accountService = accountService;
        this.verificationTokenService = verificationTokenService;
        this.fileService = fileService;
        this.userService = userService;
    }

    @PostMapping(path = "/sign-up", produces = "application/json")
    public ResponseEntity<User> createNewAccount(@Valid @RequestBody NewAccountRequest newAccountRequest){
        User createdUser = accountService.createNewUserAccount(newAccountRequest);
        return ResponseEntity.ok().body(createdUser);
    }

    @GetMapping(path = "/activate")
    public ResponseEntity<?> activateNewAccount(@RequestParam(name = "token") String token){
        accountService.enableNewUserAccount(token);
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "/profile-picture", produces = "image/png")
    public ResponseEntity<byte[]> getProfilePicture(@RequestParam(name = "picture") String pictureName){
        try{
            byte[] pictureBytes = fileService.getProfilePicture(pictureName);
            return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(pictureBytes);
        }catch (IOException e){
            throw new IllegalArgumentException("No picture under that name");
        }
    }

    @GetMapping(path = "/me", produces = "application/json")
    public ResponseEntity<User> getLoggedUser(Principal principal){
        Optional<User> currentUser = userService.findByUsername(principal.getName());
        return currentUser.map(cUser -> ResponseEntity.ok().body(cUser))
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(path = "used-space")
    public ResponseEntity<Long> getUsedSpace(Principal principal){
        return ResponseEntity.ok().body(fileService.getUsedSpace(principal));
    }

}
