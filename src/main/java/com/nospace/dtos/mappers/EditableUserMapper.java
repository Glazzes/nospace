package com.nospace.dtos.mappers;

import com.nospace.dtos.EditableUserDto;
import com.nospace.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EditableUserMapper {
    EditableUserMapper mapper = Mappers.getMapper(EditableUserMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "nickname", target = "nickname")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "memberSince", target = "memberSince")
    @Mapping(source = "profilePicture", target = "profilePicture")
    EditableUserDto userToUserDto(User user);
}
