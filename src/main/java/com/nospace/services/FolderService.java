package com.nospace.services;

import com.nospace.Repository.FolderRepository;
import com.nospace.entities.Folder;
import com.nospace.entities.User;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class FolderService {
    private final FolderRepository folderRepository;
    public FolderService(FolderRepository folderRepository) {
        this.folderRepository = folderRepository;
    }

    private String idGenerator(){
        return UUID.randomUUID().toString().replaceAll("-", "")
            .substring(0, 17);
    }

    public Optional<Folder> findById(String id){
        return folderRepository.findById(id);
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
