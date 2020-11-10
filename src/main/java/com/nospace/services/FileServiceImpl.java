package com.nospace.services;

import com.nospace.Repository.FileRepository;
import com.nospace.entities.File;
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
import java.util.UUID;

@Service
public class FileServiceImpl {

    @Value("${drive.base-path}")
    private String DRIVE_FOLDER;

    private final FileRepository repo;
    public FileServiceImpl(FileRepository repo) {
        this.repo = repo;
    }

    private void fileUpload(MultipartFile file) throws IOException {
        Path destinationRoute = Paths.get(DRIVE_FOLDER, file.getOriginalFilename());
        file.transferTo(destinationRoute);
    }

    private void saveFileInfoToDb(MultipartFile file){
        File newFile = File.builder()
            .fileSize(file.getSize())
            .uploadedAt(LocalDate.now())
            .storedIn("/root")
            .id(UUID.randomUUID().toString().substring(0, 10).replaceAll("-", ""))
            .filename(file.getOriginalFilename())
            .uploadedBy(null)
            .build();

        repo.save(newFile);
    }

    public void saveFilesToDisk(List<MultipartFile> files){
       files.forEach(file -> {
               try{
                   fileUpload(file);
                   saveFileInfoToDb(file);
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

}
