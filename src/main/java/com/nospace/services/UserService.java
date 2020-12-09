package com.nospace.services;

import com.nospace.repository.UserRepository;
import com.nospace.entities.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public User getUserByUsername(String username){
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Could not get any user by the username" + username));
    }

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User save(User newUser){
        return userRepository.save(newUser);
    }

    public Optional<User> findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByUsername(String username){
        return userRepository.findByUsername(username);
    }
}
