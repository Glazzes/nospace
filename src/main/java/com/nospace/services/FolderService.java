package com.nospace.services;

import com.nospace.repository.FolderRepository;
import com.nospace.entities.Folder;
import com.nospace.entities.User;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Service
public class FolderService {
    private final FolderRepository folderRepository;
    private final UserService userService;
    public FolderService(FolderRepository folderRepository, UserService userService) {
        this.folderRepository = folderRepository;
        this.userService = userService;
    }

    @Value("${drive.base-path}")
    private String ROOT_FOLDER;

    private String idGenerator(){
        return UUID.randomUUID().toString().replaceAll("-", "")
            .substring(0, 17);
    }

    public void saveRootFolder(User owner){
        String rootFolderRoute = String.format("%s-%s/", owner.getId(), "root");

        Folder rootFolder = Folder.builder()
            .id(idGenerator())
            .fullRoute(rootFolderRoute)
            .folderName("root")
            .depth(1)
            .owner(owner)
            .build();

        folderRepository.save(rootFolder);
        this.saveFolderToDisk(rootFolderRoute);
    }

    @Async
    public void saveFolderToDisk(String fullRoute){
        Path newPhysicalFolder = Paths.get(ROOT_FOLDER, fullRoute);
        try{
            Files.createDirectory(newPhysicalFolder);
        }catch (IOException e){
            throw new IllegalArgumentException("could not create new physical folder at "+fullRoute);
        }
    }

    public Folder findById(String folderId){
        return folderRepository.findById(folderId)
            .orElseThrow(() -> new IllegalArgumentException("No folder was found under the id "+folderId));
    }

    public Folder findByDepthAndNameAndOwner(long depth, String name, String username){
        User folderOwner = userService.getUserByUsername(username);

        return folderRepository.findByDepthAndFolderNameAndOwner(depth, name, folderOwner)
            .orElseThrow(() -> {
                String message = String.format("We could not find any folder with the given parameters " +
                "depth: %s, name: %s, owner-id: %s", depth, name, folderOwner.getId());

                throw new IllegalArgumentException(message);
            });
    }

    public Folder createNewFolder(Folder baseFolder, String newFolderName){
        String fullRoute = String.format("%s%s/", baseFolder.getFullRoute(), newFolderName);

        Folder newFolder = Folder.builder()
            .id(idGenerator())
            .folderName(newFolderName)
            .fullRoute(fullRoute)
            .depth(baseFolder.getDepth()+1)
            .owner(baseFolder.getOwner())
            .baseFolder(baseFolder)
            .subFolders(new ArrayList<>())
            .files(new ArrayList<>())
            .build();

        saveFolderToDisk(newFolder.getFullRoute());
        return folderRepository.save(newFolder);
    }

    public void deleteFolder(String folderId){
        Folder folderToDelete = this.findById(folderId);
        deleteFromDatabase(folderToDelete);
        deletePhysicalFolder(folderToDelete.getFullRoute());
    }

    private void deleteFromDatabase(Folder folderToDelete){
        folderRepository.delete(folderToDelete);
    }

    @Async
    private void deletePhysicalFolder(String folderToDelete){
        Path folder = Paths.get(ROOT_FOLDER, folderToDelete);
        try{
            FileUtils.deleteDirectory(folder.toFile());
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public Optional<Folder> findByNameAndOwner(String name, User owner){
        return folderRepository.findByFullRouteAndOwner(name, owner);
    }

}
