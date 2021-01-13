package com.nospace.services;

import com.nospace.dtos.UserDto;
import com.nospace.dtos.mappers.UserMapperImpl;
import com.nospace.entities.User;
import com.nospace.entities.VerificationToken;
import com.nospace.exception.InvalidVerificationToken;
import com.nospace.model.EditUserRequest;
import com.nospace.model.NewAccountRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    @Value("${profile-picture.default-picture-url}")
    String DEFAULT_PICTURE_URL;

    private final UserService userService;
    private final VerificationTokenService verificationTokenService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final FolderService folderService;

    private User saveNewUserToDatabase(NewAccountRequest newAccountRequest){
        String encodedPassword = encodePassword(newAccountRequest.getPassword());
        String id = UUID.randomUUID().toString().replaceAll("-", "")
            .substring(0, 11);

        User newUser = new User(id, encodedPassword, DEFAULT_PICTURE_URL, newAccountRequest);
        return userService.save(newUser);
    }

    private String encodePassword(String rawPassword){
        return passwordEncoder.encode(rawPassword);
    }

    @Transactional
    public User createNewUserAccount(NewAccountRequest newAccountRequest){
        User newUser = saveNewUserToDatabase(newAccountRequest);
        VerificationToken token = verificationTokenService.createNewVerificationToken(newUser);
        folderService.saveRootFolder(newUser);

        try{
            emailService.sendAccountVerificationEmail(token);
        }catch (MessagingException e){
           e.printStackTrace();
        }

        return newUser;
    }

    @Transactional
    public void enableNewUserAccount(String token){
        Optional<VerificationToken> existingToken = verificationTokenService.findVerificationTokenByToken(token);
        existingToken.ifPresentOrElse(
                verificationToken -> {
                    User user = verificationToken.getUser();
                    LocalDateTime expireDate = verificationToken.getExpirationDate();
                    if(expireDate.isAfter(LocalDateTime.now()) && !user.isActive()){
                        user.setActive(true);
                        userService.save(user);
                    }else {
                        String message = "This token has expired or has been already activated";
                        throw new InvalidVerificationToken(message);
                    }
                },
                () -> {throw new InvalidVerificationToken("This token does not exists");}
        );
    }

    public UserDto editAccount(EditUserRequest request, String username){
        User user = userService.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("No user with susername " + username));
        user.setNickname(request.getUsername());
        String newPassword;

        if(request.getPassword().equals("")){
            newPassword = user.getPassword();
        }else{
            newPassword = passwordEncoder.encode(request.getPassword());
        }

        user.setPassword(newPassword);

        return UserMapperImpl.INSTANCE.userToUserDto(userService.save(user));
    }

}
