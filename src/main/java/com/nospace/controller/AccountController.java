package com.nospace.controller;

import com.nospace.entities.User;
import com.nospace.model.NewAccountRequest;
import com.nospace.services.AccountService;
import com.nospace.services.VerificationTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/account")
@CrossOrigin("*")
public class AccountController {

    private final AccountService accountService;
    private final VerificationTokenService verificationTokenService;
    public AccountController(AccountService accountService, VerificationTokenService verificationTokenService) {
        this.accountService = accountService;
        this.verificationTokenService = verificationTokenService;
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

}
