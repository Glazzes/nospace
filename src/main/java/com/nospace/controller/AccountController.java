package com.nospace.controller;

import com.nospace.entities.User;
import com.nospace.model.NewAccountRequest;
import com.nospace.services.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;
    private final VerificationTokenService verificationTokenService;
    private final UserService userService;
    private final FileService fileService;

    public AccountController(
        AccountService accountService,
        VerificationTokenService verificationTokenService,
        UserService userService,
        FileService fileService
    ) {
        this.accountService = accountService;
        this.verificationTokenService = verificationTokenService;
        this.userService = userService;
        this.fileService = fileService;
    }

    @PostMapping(path = "/register", produces = "application/json")
    public ResponseEntity<User> createNewAccount(@Valid @RequestBody NewAccountRequest newAccountRequest){
        User createdUser = accountService.createNewUserAccount(newAccountRequest);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping(path = "/activate")
    public ResponseEntity<?> activateNewAccount(@RequestParam(name = "token") String token){
        accountService.enableNewUserAccount(token);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(path = "/me", produces = "application/json")
    public ResponseEntity<User> getLoggedUser(Principal principal){
        Optional<User> currentUser = userService.findByUsername(principal.getName());
        return currentUser.map(cUser -> ResponseEntity.ok().body(cUser))
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(path = "/profile-picture/{picture}", produces = "image/png")
    public ResponseEntity<byte[]> getProfilePicture(
        @PathVariable(name = "picture") String picture
    ){
        byte[] profilePicture = fileService.getProfilePicture(picture);
        return ResponseEntity.ok()
            .body(profilePicture);
    }

    //@GetMapping(path = "used-space")
    //public ResponseEntity<Long> getUsedSpace(Principal principal){
    //    return ResponseEntity.ok().body(fileService.getUsedSpace(principal));
    //}

}
