package com.example.pos.system.service.impl;

import com.example.pos.system.configuration.JwtProvider;
import com.example.pos.system.exception.UserException;
import com.example.pos.system.modal.User;
import com.example.pos.system.repository.UserRepository;
import com.example.pos.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @Override
    public User getUserFromJwtToken(String token) throws UserException {
        String email = jwtProvider.getEmailFromToken(token);
        User user = userRepository.findByEmail(email);

        if (user==null){
            throw new UserException("Invalid token");
        }
        return user;
    }

    @Override
    public User getCurrentUser() throws UserException {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email);
        if (user==null){
            throw new UserException("User not found..");
        }
        return user;
    }

    @Override
    public User getUserByEmail(String email) throws UserException {
        User user = userRepository.findByEmail(email);
        if (user==null){
            throw new UserException("User not found..");
        }
        return user;
    }

    @Override
    public User getUserById(Long id) throws UserException, Exception {
        return userRepository.findById(id).orElseThrow(
                ()->new Exception("user not found..")
        );
    }

    @Override
    public List<User> getAllUser() {
        return userRepository.findAll();
    }
}
