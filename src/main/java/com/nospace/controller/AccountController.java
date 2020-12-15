package com.nospace.controller;

import com.nospace.entities.User;
import com.nospace.model.NewAccountRequest;
import com.nospace.services.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    private final SpaceUtil spaceUtil;

    public AccountController(
        AccountService accountService,
        VerificationTokenService verificationTokenService,
        UserService userService,
        FileService fileService,
        SpaceUtil spaceUtil
    ) {
        this.accountService = accountService;
        this.verificationTokenService = verificationTokenService;
        this.userService = userService;
        this.fileService = fileService;
        this.spaceUtil = spaceUtil;
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

    @PostMapping(path = "/{id}/profile-picture", consumes = "multipart/form-data")
    public ResponseEntity<User> updateUserProfilePicture(
        @PathVariable(name = "id") String id,
        @RequestPart(name = "file") MultipartFile file
        ){
        User user = userService.findById(id);
        User savedUser = fileService.updateUserProfilePicture(user, file);
        return ResponseEntity.status(HttpStatus.CREATED)
                   .body(savedUser);
    }

    @GetMapping(path = "/used-space")
    public ResponseEntity<Long> getUserUsedSpace(Principal principal){
        User user = userService.findByUsername(principal.getName())
            .orElseThrow(() -> new UsernameNotFoundException("No user was found with username " + principal.getName()));
        String rootFolder = String.format("%s-%s", user.getId(), "root");

        return ResponseEntity.ok()
            .body(spaceUtil.getFolderUsedSpace(rootFolder));
    }

}
