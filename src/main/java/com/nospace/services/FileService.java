package com.nospace.services;

import com.nospace.entities.File;
import com.nospace.entities.Folder;
import com.nospace.entities.User;
import com.nospace.exception.StorageCapacityExceededException;
import com.nospace.repository.FileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FileService {

    @Value("${drive.picture-storage}")
    private String PICTURE_STORAGE;

    @Value("${drive.base-path}")
    private String BASE_PATH;

    @Value("${profile-picture.base-url}")
    private String PICTURE_URL;

    private final FileRepository fileRepository;
    private final FolderService folderService;
    private final UserService userService;
    private final SpaceUtil spaceUtil;

    public FileService(
        FolderService folderService,
        FileRepository fileRepository,
        UserService userService,
        SpaceUtil spaceUtil
    ){
        this.folderService = folderService;
        this.fileRepository = fileRepository;
        this.userService = userService;
        this.spaceUtil = spaceUtil;
    }

    public File findById(String id){
        return fileRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("No file found with id " + id));
    }

    @Transactional(rollbackOn = {StorageCapacityExceededException.class, IOException.class})
    public List<File> saveNewFiles(String baseFolderId, List<MultipartFile> files){
        Folder baseFolder = folderService.findById(baseFolderId);
        String rootFolder = String.format(
            "%s-%s/", baseFolder.getOwner().getId(), "rot"
        );

        long rootFolderSize = spaceUtil.getFolderUsedSpace(rootFolder);
        long fileCountSize = files.stream()
            .mapToLong(MultipartFile::getSize)
            .sum();

        if(spaceUtil.exceedsMaximumCapacity(rootFolderSize+fileCountSize)){
            throw new StorageCapacityExceededException("Files could no be uploaded because the exceeded the 1gb limit");
        }

        return files.stream()
            .map(file -> {
                String fileRoute = baseFolder.getFullRoute() + file.getOriginalFilename();
                saveFileToDisk(BASE_PATH, fileRoute, file);
                return saveFileToDb(baseFolder, file.getOriginalFilename(), file.getSize());
            })
            .collect(Collectors.toList());

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
    private void saveFileToDisk(String predefinedRoute, String diskRoute, MultipartFile file){
        Path fileDestination = Paths.get(predefinedRoute, diskRoute);
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

    public void deleteFile(String fileId){
        File deletedFile = deleteFileFromDatabase(fileId);
        deleteFileFromDisk(deletedFile);
    }

    private File deleteFileFromDatabase(String id){
        File fileToDelete = fileRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Could not find file with id " + id));

        fileRepository.delete(fileToDelete);
        return fileToDelete;
    }

    private void deleteFileFromDisk(File deletedFile){
        Path fileLocation = Paths.get(BASE_PATH, deletedFile.getFullRoute());
        try{
            Files.deleteIfExists(fileLocation);
        }catch (IOException e){
            String message = String.format(
                "Error deleting file, we could not delete file with id %s at %s",
                deletedFile.getId(), deletedFile.getFullRoute()
            );

            log.info(message);
            e.printStackTrace();
        }
    }

    public byte[] prepareFileForDownload(File fileToDownload){
        Path fileLocation = Paths.get(BASE_PATH, fileToDownload.getFullRoute());

        try{
            log.info(fileLocation.toString());
            return Files.readAllBytes(fileLocation);
        }catch (IOException e){
            String message = String.format("Could not find file with id %s in %s",
                fileToDownload.getId(), fileToDownload.getFullRoute());

            log.info(message);
            e.printStackTrace();
            throw new IllegalArgumentException("No file found");
        }
    }

    public String determineContentType(File file){
        Path fileLocation = Paths.get(BASE_PATH, file.getFullRoute());
        try{
            return Files.probeContentType(fileLocation);
        }catch (IOException e){
            log.info("Could not read content file for file " + file.getFilename());
            throw new IllegalArgumentException();
        }
    }

    public User updateUserProfilePicture(User user, MultipartFile file){
        String newFilename = renameFile(file.getOriginalFilename());
        saveFileToDisk(PICTURE_STORAGE, newFilename, file);
        user.setProfilePicture(PICTURE_URL+newFilename);
        user.setFolders(new ArrayList<>());
        return userService.save(user);
    }

    private String renameFile(String filename){
        Pattern pattern = Pattern.compile(".*\\.(png|jpg|jpeg)$");
        String extension = pattern.matcher(filename).replaceAll("$1");
        String randomIdentifier = UUID.randomUUID().toString().substring(0, 11).replaceAll("-", "");
        return String.format("%s.%s", randomIdentifier, extension);
    }

}
