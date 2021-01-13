package com.nospace.dtos;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class FolderDto {
    private String id;
    private String folderName;
}
