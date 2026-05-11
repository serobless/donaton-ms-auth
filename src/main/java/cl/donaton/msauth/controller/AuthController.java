package cl.donaton.msauth.controller;

import cl.donaton.msauth.dto.AuthResponse;
import cl.donaton.msauth.dto.LoginRequest;
import cl.donaton.msauth.dto.RegisterRequest;
import cl.donaton.msauth.dto.UsuarioDto;
import cl.donaton.msauth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioDto> me() {
        return ResponseEntity.ok(authService.getCurrentUser());
    }
}
