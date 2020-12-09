package com.nospace.services;

import com.nospace.entities.File;
import com.nospace.entities.Folder;
import com.nospace.repository.FileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Service
public class FileService {

    @Value("${drive.picture-storage}")
    private String PICTURE_STORAGE;

    @Value("${drive.base-path}")
    private String BASE_PATH;

    private final FileRepository fileRepository;
    private final FolderService folderService;
    public FileService(FolderService folderService, FileRepository fileRepository){
        this.folderService = folderService;
        this.fileRepository = fileRepository;
    }

    public File saveNewFile(String baseFolderId, MultipartFile file){
        Folder baseFolder = folderService.findById(baseFolderId);
        File savedFile = saveFileToDb(baseFolder, file.getOriginalFilename(), file.getSize());
        saveFileToDisk(savedFile.getFullRoute(), file);

        return savedFile;
    }

    private File saveFileToDb(Folder baseFolder, String fileName, long size){
        String id = UUID.randomUUID().toString().replaceAll("-", "")
            .substring(0, 11);

        File newFile = File.builder()
            .id(id)
            .filename(fileName)
            .fileSize(size)
            .fullRoute(baseFolder.getFullRoute()+fileName)
            .containingFolder(baseFolder)
            .uploadedAt(LocalDate.now())
            .build();

        return fileRepository.save(newFile);
    }

    @Async
    private void saveFileToDisk(String diskRoute, MultipartFile file){
        Path fileDestination = Paths.get(BASE_PATH, diskRoute);
        try{
            Files.write(fileDestination, file.getBytes());
        }catch (IOException e){
            String message = String.format("Error saving file %s on destination %s",
                file.getOriginalFilename(), diskRoute);

            log.info(message);
            e.printStackTrace();
        }
    }

    public byte[] getProfilePicture(String pictureName){
        Path pictureLocation = Paths.get(PICTURE_STORAGE, pictureName);
        try{
            return Files.readAllBytes(pictureLocation);
        }catch (IOException e){
            String message = String.format("Could not retrieve profile picture on %s with name %s", PICTURE_STORAGE, pictureName);
            throw new IllegalArgumentException(message);
        }
    }

}
