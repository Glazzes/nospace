package com.nospace.controller;

import com.nospace.dtos.EditableUserDto;
import com.nospace.dtos.UserDto;
import com.nospace.dtos.mappers.EditableUserMapper;
import com.nospace.dtos.mappers.UserMapperImpl;
import com.nospace.entities.User;
import com.nospace.model.EditUserRequest;
import com.nospace.model.NewAccountRequest;
import com.nospace.services.*;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final VerificationTokenService verificationTokenService;
    private final UserService userService;
    private final FileService fileService;
    private final SpaceUtil spaceUtil;

    @PostMapping(path = "/register", produces = "application/json")
    public ResponseEntity<UserDto> createNewAccount(@Valid @RequestBody NewAccountRequest newAccountRequest){
        User createdUser = accountService.createNewUserAccount(newAccountRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(UserMapperImpl.INSTANCE.userToUserDto(createdUser));
    }

    @GetMapping(path = "/activate")
    public ResponseEntity<?> activateNewAccount(@RequestParam(name = "token") String token){
        accountService.enableNewUserAccount(token);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(path = "/me", produces = "application/json")
    public ResponseEntity<UserDto> getLoggedUser(Principal principal){
        Optional<User> currentUser = userService.findByUsername(principal.getName());
        return currentUser.map(cUser -> {
            UserDto user = UserMapperImpl.INSTANCE.userToUserDto(cUser);
            return ResponseEntity.ok().body(user);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(path = "/edit/profile-picture", consumes = "multipart/form-data", produces = "application/json")
    public ResponseEntity<UserDto> updateUserProfilePicture(
        @RequestPart(name = "file") MultipartFile file,
        Principal principal
    ){
        User user = userService.findByUsername(principal.getName())
            .orElseThrow(() -> new UsernameNotFoundException("No user found with username "+ principal.getName()));
        UserDto savedUser = fileService.updateUserProfilePicture(user, file);
        return ResponseEntity.status(HttpStatus.CREATED)
                   .body(savedUser);
    }

    @PostMapping(path = "/edit", consumes = "application/json", produces = "application/json")
    public ResponseEntity<UserDto> editAccountDetails(
        @Valid @RequestBody EditUserRequest request,
        Principal principal
    ){
        UserDto editedUser = accountService.editAccount(request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(editedUser);
    }

    @GetMapping(path = "/profile-picture/{picture}", produces = "image/png")
    public ResponseEntity<byte[]> getProfilePicture(
        @PathVariable(name = "picture") String picture
    ){
        byte[] profilePicture = fileService.getProfilePicture(picture);
        return ResponseEntity.ok()
            .body(profilePicture);
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
