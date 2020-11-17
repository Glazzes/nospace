package com.nospace.controller;

import com.nospace.entities.File;
import com.nospace.entities.User;
import com.nospace.services.FileServiceImpl;
import com.nospace.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/file")
@CrossOrigin("*")
public class FileController {

    private final FileServiceImpl fileService;
    private final UserService userService;
    public FileController(FileServiceImpl fileService, UserService userService) {
        this.fileService = fileService;
        this.userService = userService;
    }

    @PostMapping(path = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<?> multipleFileUpload(
        @RequestPart(name = "file") List<MultipartFile> files,
        @RequestParam(name = "store", required = false) String folderPath,
        Principal principal
    )throws IOException {
        String folder = Optional.ofNullable(folderPath)
            .orElseGet(() -> {
                Optional<User> user = userService.findByUsername(principal.getName());
                return user.get().getId()+"-root/";
            });

        fileService.saveFilesToDisk(files, folder, principal.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "/my-files")
    public ResponseEntity<List<File>> getUserFiles(
        @RequestParam(name = "folder", required = false) String folderName,
        Principal principal
    ){
        Optional<User> user = userService.findByUsername(principal.getName());
        String folder = Optional.ofNullable(folderName)
            .orElseGet(() -> user.get().getId()+"-root/");

        List<File> files =  fileService.getUserFiles(folder, user.get());
        return ResponseEntity.ok().body(files);
    }

}
