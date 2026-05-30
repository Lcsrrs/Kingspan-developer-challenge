package com.kingspan.challenge.auth;

import com.kingspan.challenge.auth.dto.AuthresponseDTO;
import com.kingspan.challenge.auth.dto.LoginRequestDTO;
import com.kingspan.challenge.auth.dto.RegisterRequestDTO;
import com.kingspan.challenge.common.security.JwtService;
import com.kingspan.challenge.users.User;
import com.kingspan.challenge.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;


    public AuthresponseDTO register(RegisterRequestDTO registerRequest){

        if (userRepository.existsByEmail(registerRequest.email())) {
            throw new RuntimeException("Email já cadastrado");
        }

        var entity = User.builder()
                .name(registerRequest.name())
                .email(registerRequest.email())
                .passwordHash(passwordEncoder.encode(registerRequest.password()))
                .role(registerRequest.role())
                .approverLevel(registerRequest.approverLevel())
                .build();

        userRepository.save(entity);

        var token = jwtService.generateToken(entity);

        return new AuthresponseDTO(token, entity.getName(), entity.getEmail(), entity.getRole());
    }

    public Object getCurrentUser() {
        //TODO
        return null;
    }

    public AuthresponseDTO login(LoginRequestDTO request){

        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Usuário ou senha incorretos"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash()))
            throw new RuntimeException("Usuário ou senha incorretos");

        var token = jwtService.generateToken(user);

        return new AuthresponseDTO(token, user.getName(), user.getEmail(), user.getRole());
    }

}
