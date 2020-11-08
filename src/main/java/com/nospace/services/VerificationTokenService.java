package com.nospace.services;

import com.nospace.Repository.VerificationTokenRepository;
import com.nospace.entities.User;
import com.nospace.entities.VerificationToken;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class VerificationTokenService {

    private final VerificationTokenRepository verificationTokenRepository;
    public VerificationTokenService(VerificationTokenRepository verificationTokenRepository) {
        this.verificationTokenRepository = verificationTokenRepository;
    }

    public VerificationToken createNewVerificationToken(User user){
        String token = UUID.randomUUID().toString().replaceAll("-", "");

        VerificationToken newVerificationToken = new VerificationToken();
        newVerificationToken.setToken(token);
        newVerificationToken.setExpirationDate(LocalDateTime.now().plusDays(1));
        newVerificationToken.setUser(user);

        return verificationTokenRepository.save(newVerificationToken);
    }

    public Optional<VerificationToken> findVerificationTokenByToken(String token){
        return verificationTokenRepository.findByToken(token);
    }

}
