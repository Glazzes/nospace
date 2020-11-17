package com.nospace.services;

import com.nospace.Repository.FolderRepository;
import com.nospace.entities.Folder;
import com.nospace.entities.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Service
public class FolderService {
    private final FolderRepository folderRepository;
    public FolderService(FolderRepository folderRepository) {
        this.folderRepository = folderRepository;
    }

    @Value("${drive.base-path}")
    private String ROOT_FOLDER;

    private String idGenerator(){
        return UUID.randomUUID().toString().replaceAll("-", "")
            .substring(0, 17);
    }

    public Optional<Folder> findById(String id){
        return folderRepository.findById(id);
    }

    @Async
    public void createRootFolderForUser(String folderName){
        Path newRootFolder = Paths.get(ROOT_FOLDER, folderName);
        try{
            Files.createDirectory(newRootFolder);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Async
    public void createNewPhysicalFolder(String path, String folderName){
        Path newRootFolder = Paths.get(ROOT_FOLDER, path, folderName);
        try{
            Files.createDirectory(newRootFolder);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public Optional<Folder> findByNameAndOwner(String name, User owner){
        return folderRepository.findByNameAndOwner(name, owner);
    }

    public Folder saveFolder(User owner, String name){
        String folderId = idGenerator();
        Folder newFolder = new Folder(folderId, name, owner);

        return folderRepository.save(newFolder);
    }
}
