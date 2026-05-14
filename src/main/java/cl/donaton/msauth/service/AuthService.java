package cl.donaton.msauth.service;

import cl.donaton.msauth.dto.AuthResponse;
import cl.donaton.msauth.dto.LoginRequest;
import cl.donaton.msauth.dto.RegisterRequest;
import cl.donaton.msauth.dto.UsuarioDto;
import cl.donaton.msauth.entity.Rol;
import cl.donaton.msauth.entity.Usuario;
import cl.donaton.msauth.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        var usuario = Usuario.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .rol(request.getRol() != null ? request.getRol() : Rol.DONANTE)
                .fechaRegistro(LocalDateTime.now())
                .build();
        usuarioRepository.save(usuario);
        String token = jwtService.generateToken(Map.of("roles", usuario.getRol()), usuario);
        return new AuthResponse(token, usuario.getEmail(), usuario.getRol());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        var usuario = usuarioRepository.findByEmail(request.getEmail()).orElseThrow();
        String token = jwtService.generateToken(Map.of("roles", usuario.getRol()), usuario);
        return new AuthResponse(token, usuario.getEmail(), usuario.getRol());
    }

    public UsuarioDto getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var usuario = (Usuario) authentication.getPrincipal();
        return UsuarioDto.fromUsuario(usuario);
    }
}
