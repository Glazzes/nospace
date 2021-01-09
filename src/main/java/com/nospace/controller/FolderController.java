package com.nospace.controller;

import com.nospace.entities.Folder;
import com.nospace.services.FolderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/content")
public class FolderController {
    private final FolderService folderService;

    public FolderController(FolderService folderService) {
        this.folderService = folderService;
    }

    @PostMapping(path = "/new", produces = "application/json")
    public ResponseEntity<Folder> createNewFolder(
        @RequestParam(name = "baseId") String baseFolderId,
        @RequestParam(name = "name") String newFolderName
    ){
        Folder baseFolder = folderService.findById(baseFolderId);
        Folder updatedBaseFolder = folderService.createNewFolder(baseFolder, newFolderName);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(updatedBaseFolder);
    }

    @GetMapping(path = "/my-content", produces = "application/json")
    public ResponseEntity<Folder> getUserFolders(
        @RequestParam(name = "depth") long depth,
        @RequestParam(name = "name") String name,
        Principal principal
    ){
        Folder content = folderService.findByDepthAndNameAndOwner(depth, name, principal.getName());
        return ResponseEntity.ok().body(content);
    }

    @GetMapping(path = "/{id}/download", produces = "application/zip")
    public ResponseEntity<byte[]> downloadFolder(
        @PathVariable(name = "id") String folderId
    ) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(folderService.zipFolderContents(folderId));
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteFolder(@PathVariable(name = "id") String folderId){
        folderService.deleteFolder(folderId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
