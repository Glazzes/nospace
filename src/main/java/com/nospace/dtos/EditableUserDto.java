package com.nospace.dtos;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class EditableUserDto {
    private String id;
    private String username;
    private String nickname;
    private String password;
    private String email;
    private String memberSince;
    private String profilePicture;
}
