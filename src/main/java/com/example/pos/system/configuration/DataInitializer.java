package com.example.pos.system.configuration;

import com.example.pos.system.domain.UserRole;
import com.example.pos.system.modal.User;
import com.example.pos.system.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public DataInitializer(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public void run(String... args) throws Exception {


        if(userRepository.findByEmail("superadmin@gmail.com") == null){

            User existing = userRepository.findByEmail("superadmin@gmail.com");

            if (existing == null) {
                User user = new User();
                user.setFullName("Super Admin");
                user.setEmail("superadmin@gmail.com");
                user.setPassword(passwordEncoder.encode("123456"));
                user.setRole(UserRole.ROLE_SUPER_ADMIN);
                userRepository.save(user);
                System.out.println("SUPER ADMIN CREATED");
            } else if (existing.getRole() == null) {
                existing.setRole(UserRole.ROLE_SUPER_ADMIN);
                userRepository.save(existing);
                System.out.println("SUPER ADMIN ROLE FIXED");
            }
        }

    }
}