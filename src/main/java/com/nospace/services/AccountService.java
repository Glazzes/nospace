package com.nospace.services;

import com.nospace.entities.User;
import com.nospace.entities.VerificationToken;
import com.nospace.exception.InvalidVerificationToken;
import com.nospace.model.NewAccountRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AccountService {

    private final UserService userService;
    private final VerificationTokenService verificationTokenService;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final FolderService folderService;

    @Value("${profile-picture.default-picture-url}")
    String DEFAULT_PICTURE_URL;

    public AccountService(
            UserService userService,
            VerificationTokenService verificationTokenService,
            EmailService emailService,
            BCryptPasswordEncoder passwordEncoder,
            FolderService folderService
    ) {
        this.userService = userService;
        this.verificationTokenService = verificationTokenService;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.folderService = folderService;
    }

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

}
