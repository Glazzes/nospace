package com.nospace.dtos.mappers;

import com.nospace.dtos.FolderDto;
import com.nospace.entities.Folder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FolderMapper {
    FolderMapper mapper = Mappers.getMapper(FolderMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "folderName", target = "folderName")
    FolderDto folderToFolderDto(Folder folder);
}
