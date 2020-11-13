package com.nospace.services;

import com.nospace.Repository.FileRepository;
import com.nospace.entities.File;
import com.nospace.entities.Folder;
import com.nospace.entities.User;
import com.nospace.exception.FileNotWrittenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FileServiceImpl {

    @Value("${drive.base-path}")
    private String DRIVE_FOLDER;

    private final FileRepository fileRepository;
    private final FolderService folderService;
    private final UserService userService;
    public FileServiceImpl(FileRepository fileRepository, FolderService folderService, UserService userService) {
        this.fileRepository = fileRepository;
        this.folderService = folderService;
        this.userService = userService;
    }

    private void fileUpload(MultipartFile file) throws IOException {
        Path destinationRoute = Paths.get(DRIVE_FOLDER, file.getOriginalFilename());
        file.transferTo(destinationRoute);
    }

    private void saveFileInfoToDb(MultipartFile file, String folderName, String username){
        Optional<User> user =  userService.findByUsername(username);
        Optional<Folder> folder = folderService.findByNameAndOwner(folderName, user.get());

        folder.ifPresentOrElse(foundFolder-> {
            String id = UUID.randomUUID().toString().replaceAll("-", "")
                .substring(0, 11);

                File newFile = File.builder()
                    .id(id)
                    .filename(file.getOriginalFilename())
                    .fileSize(file.getSize())
                    .storedIn(foundFolder)
                    .uploadedAt(LocalDate.now())
                    .build();

                fileRepository.save(newFile);
        },
            () -> {throw new IllegalArgumentException("something");});
    }

    public void saveFilesToDisk(List<MultipartFile> files, String folderName, String username){
       files.forEach(file -> {
               try{
                   fileUpload(file);
                   saveFileInfoToDb(file, folderName, username);
               }catch (IOException e){
                   throw new FileNotWrittenException("Files couldn't get saved to the server");
               }
           });
    }

    public void updateProfilePicture(MultipartFile file) throws IOException{
        String fileExtension = file.getOriginalFilename()
            .replaceAll(".*(\\.\\w{3,4})$", "$1");

        String fileName = UUID.randomUUID().toString().replaceAll("-", "")
            .substring(0,17);

        String finalFilename = fileName+fileExtension;
        Path destination = Paths.get(DRIVE_FOLDER, "ppf", finalFilename);
        Files.write(destination, file.getBytes());
    }

    public byte[] getProfilePicture(String pictureName) throws IOException{
        Path picture = Paths.get(DRIVE_FOLDER, "ppf", pictureName);
        return Files.readAllBytes(picture);
    }

    public List<File> getUserFiles(String folderName, User owner){
        Optional<Folder> folder = folderService.findByNameAndOwner(folderName, owner);
        if(folder.isPresent()){
            return fileRepository.findByStoredInId(folder.get().getId());
        }

        return null;
    }
}
