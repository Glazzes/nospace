package com.nospace.controller;

import com.nospace.services.FileServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/file")
@CrossOrigin("*")
public class FileController {

    private final FileServiceImpl fileService;
    public FileController(FileServiceImpl fileService) {
        this.fileService = fileService;
    }

    @PostMapping(path = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<?> multipleFileUpload(
        @RequestPart(name = "file") List<MultipartFile> files
    )throws IOException {
        fileService.updateProfilePicture(files.get(0));
        fileService.saveFilesToDisk(files);
        return ResponseEntity.ok().build();
    }

}
