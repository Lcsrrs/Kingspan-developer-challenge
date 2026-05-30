package com.kingspan.challenge.auth;

import com.kingspan.challenge.auth.dto.Authresponse;
import com.kingspan.challenge.auth.dto.LoginRequest;
import com.kingspan.challenge.auth.dto.RegisterRequest;
import com.kingspan.challenge.users.User;
import com.kingspan.challenge.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService implements UserDetailsService {

    private UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final JwtService jwtService;


    @Autowired
    UserRepository repository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByEmail(username);
    }

    public UUID createUser(RegisterRequest registerRequest){

        var entity = new User(UUID.randomUUID(),
                registerRequest.getName(),
                registerRequest.getEmail(),
                registerRequest.getPassword(),
                registerRequest.getRole(),
                registerRequest.getApproverLevel(),
                LocalDateTime.now()
                );

        var userSaved = userRepository.save(entity);

        return userSaved.getId();
    }

    public Object getCurrentUser() {
        //TODO
        return null;
    }

    public Authresponse login(LoginRequest request){
        //TODO
        return null;
    }
}
