package com.example.pos.system.configuration;

import com.example.pos.system.domain.UserRole;
import com.example.pos.system.modal.User;
import com.example.pos.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        boolean superAdminExists = userRepository.existsByRole(UserRole.ROLE_SUPER_ADMIN);

        if (!superAdminExists) {
            User admin = new User();
            admin.setFullName("RushiKesh Chepte");
            admin.setEmail("chepterushikesh611@gmail.com");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setPhone(null);
            admin.setRole(UserRole.ROLE_SUPER_ADMIN);
            admin.setCreatedAt(LocalDateTime.now());
            admin.setUpdatedAt(LocalDateTime.now());
            admin.setStore(null);
            admin.setBranch(null);

            userRepository.save(admin);

            System.out.println("Super Admin created:  chepterushikesh611@gmail.com / 123456");
        } else {
            System.out.println("Super Admin already exists, skipping creation.");
        }
    }
}