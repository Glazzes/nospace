package com.nospace.dtos;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UserDto {
    private String id;
    private String username;
    private String nickname;
    private String email;
    private String memberSince;
    private String profilePicture;
}
