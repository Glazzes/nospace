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
        @RequestParam(name = "name") String folderName,
        @RequestParam(name = "current", required = false) String currentFolder
    ){
        Optional<User> owner = userService.findByUsername(principal.getName());
        String current = Optional.ofNullable(currentFolder)
            .orElseGet(() -> owner.get().getId()+"-root/");

        final String finalFolderName = String.format("%s%s/", current, folderName);
        folderService.saveFolder(owner.get(), finalFolderName);
        folderService.createNewPhysicalFolder(current, folderName);
        return ResponseEntity.ok().build();
    }

}
