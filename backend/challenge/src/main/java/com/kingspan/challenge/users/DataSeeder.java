package com.kingspan.challenge.users;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        createUserIfNotExists(
                "Admin",
                "admin@kingspan.com.br",
                "admin",
                UserRole.ADMIN,
                null
        );

        createUserIfNotExists(
                "Aprovador1",
                "Aprovador1@kingspan.com.br",
                "aprovador1",
                UserRole.APROVADOR,
                ApproverLevel.NIVEL_1
        );

        createUserIfNotExists(
                "Aprovador2",
                "Aprovador2@kingspan.com.br",
                "aprovador2",
                UserRole.APROVADOR,
                ApproverLevel.NIVEL_2
        );

        createUserIfNotExists(
                "Aprovador3",
                "Aprovador3@kingspan.com.br",
                "aprovador3",
                UserRole.APROVADOR,
                ApproverLevel.NIVEL_3
        );

        createUserIfNotExists(
                "Solicitante",
                "solicitante@kingspan.com.br",
                "solicitante",
                UserRole.SOLICITANTE,
                null
        );

    }



    private void createUserIfNotExists(String name, String email, String password, UserRole role, ApproverLevel approverLevel) {
        if (userRepository.existsByEmail(email)) return;

        User user = User.builder()
                .name(name)
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .role(role)
                .approverLevel(approverLevel)
                .build();

        userRepository.save(user);


    }
}
