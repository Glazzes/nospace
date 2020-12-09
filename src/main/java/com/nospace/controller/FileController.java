package com.nospace.controller;

import com.nospace.entities.File;
import com.nospace.services.FileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")
public class FileController {

    public final FileService fileService;
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(path = "/new", produces = "application/json")
    public ResponseEntity<File> createNewFile(
        @RequestParam(name = "baseId") String baseFolderId,
        @RequestPart(name = "file") MultipartFile file
    ){
        File newFile = fileService.saveNewFile(baseFolderId, file);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(newFile);
    }

}
