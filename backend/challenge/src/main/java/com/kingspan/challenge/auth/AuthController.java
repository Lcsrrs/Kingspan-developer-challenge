package com.kingspan.challenge.auth;

import com.kingspan.challenge.auth.dto.Authresponse;
import com.kingspan.challenge.auth.dto.LoginRequest;
import com.kingspan.challenge.auth.dto.RegisterRequest;
import com.kingspan.challenge.users.User;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Authresponse> createUser(@Valid @RequestBody RegisterRequest request){
        var userID = authService.createUser(request);
        return ResponseEntity.created(URI.create("/register/"+userID.toString())).build();
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(){
        return ResponseEntity.ok(authService.getCurrentUser());
    }

    @PostMapping("/login")
    public ResponseEntity<Authresponse> loginUser (@Valid @RequestBody LoginRequest request){
        Authresponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

}
