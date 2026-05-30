package com.kingspan.challenge.auth;

import com.kingspan.challenge.auth.dto.AuthresponseDTO;
import com.kingspan.challenge.auth.dto.LoginRequestDTO;
import com.kingspan.challenge.auth.dto.RegisterRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthresponseDTO> createUser(@Valid @RequestBody RegisterRequestDTO request){
        var response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(){
        return ResponseEntity.ok(authService.getCurrentUser());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthresponseDTO> loginUser (@Valid @RequestBody LoginRequestDTO request){
        AuthresponseDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }

}
