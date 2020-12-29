package com.nospace.controller;

import com.nospace.entities.File;
import com.nospace.services.FileService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/files")
public class FileController {

    public final FileService fileService;
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(path = "/new", produces = "application/json")
    public ResponseEntity<List<File>> createNewFile(
        @RequestParam(name = "baseId") String baseFolderId,
        @RequestPart(name = "file") List<MultipartFile> file
    ){
        List<File> newFiles = fileService.saveNewFiles(baseFolderId, file);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(newFiles);
    }

    @DeleteMapping(path= "/{id}")
    public ResponseEntity<Void> deleteFileById(@PathVariable(name = "id") String fileId){
        HttpHeaders allowDelete = new HttpHeaders();
        allowDelete.add("Access-Control-Allow-Methods", "DELETE");

        fileService.deleteFile(fileId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
            .headers(allowDelete)
            .build();
    }

    @GetMapping(path = "/{id}/download")
    public ResponseEntity<byte[]> downloadFile(@PathVariable(name = "id") String id){
        File fileToDownload = fileService.findById(id);
        final String CONTENT_TYPE = fileService.determineContentType(fileToDownload);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", CONTENT_TYPE);

        return ResponseEntity.status(HttpStatus.OK)
            .headers(headers)
            .body(fileService.prepareFileForDownload(fileToDownload));
    }

}
