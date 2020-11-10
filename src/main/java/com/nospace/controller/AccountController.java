package com.nospace.controller;

import com.nospace.entities.User;
import com.nospace.model.NewAccountRequest;
import com.nospace.services.AccountService;
import com.nospace.services.FileServiceImpl;
import com.nospace.services.VerificationTokenService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping("/account")
@CrossOrigin("*")
public class AccountController {

    private final AccountService accountService;
    private final VerificationTokenService verificationTokenService;
    private final FileServiceImpl fileService;
    public AccountController(
        AccountService accountService,
        VerificationTokenService verificationTokenService,
        FileServiceImpl fileService
    ) {
        this.accountService = accountService;
        this.verificationTokenService = verificationTokenService;
        this.fileService = fileService;
    }

    @PostMapping(path = "/sign-up", produces = "application/json")
    public ResponseEntity<User> createNewAccount(@Valid @RequestBody NewAccountRequest newAccountRequest){
        User createdUser = accountService.createNewUserAccount(newAccountRequest);
        URI newUserUrl = URI.create("http://localhost:8080/user/"+createdUser.getUsername());
        return ResponseEntity.created(newUserUrl).body(createdUser);
    }

    @GetMapping(path = "/activate")
    public ResponseEntity<?> activateNewAccount(@RequestParam(name = "token", required = true) String token){
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

}
