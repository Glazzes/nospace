package com.nospace.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@Slf4j
public class SpaceUtil {

    @Value("${drive.base-path}")
    private String BASE_PATH;

    @Value("${drive.max-default-capacity}")
    private long MAX_DEFAULT_CAPACITY;

    public long getFolderUsedSpace(String route){
        try{
            return Files.walk(Paths.get(BASE_PATH, route))
            .map(Path::toFile)
            .filter(File::isFile)
            .mapToLong(File::length)
            .sum();
        }catch (IOException e){
            String message = String.format("Size couldn't be read for location " + route);
            log.info(message);
            e.printStackTrace();
            return -1;
        }
    }

    public boolean exceedsMaximumCapacity(long currentSpace){
        return currentSpace > MAX_DEFAULT_CAPACITY;
    }


}
