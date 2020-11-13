package com.nospace.controller;

import com.nospace.entities.User;
import com.nospace.services.FolderService;
import com.nospace.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/folder")
@CrossOrigin("*")
public class FolderController {
    private final FolderService folderService;
    private final UserService userService;

    public FolderController(FolderService folderService, UserService userService) {
        this.folderService = folderService;
        this.userService = userService;
    }

    @PostMapping(path = "/new")
    public ResponseEntity<?> saveNewFolder(
        Principal principal,
        @RequestParam(name = "folder") String folderName
    ){
        Optional<User> owner = userService.findByUsername(principal.getName());
        folderService.saveFolder(owner.get(), folderName);
        return ResponseEntity.ok().build();
    }

}
