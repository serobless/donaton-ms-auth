package cl.donaton.msauth.service;

import cl.donaton.msauth.dto.AuthResponse;
import cl.donaton.msauth.dto.LoginRequest;
import cl.donaton.msauth.dto.RegisterRequest;
import cl.donaton.msauth.entity.Rol;
import cl.donaton.msauth.entity.Usuario;
import cl.donaton.msauth.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private Usuario usuarioExistente;

    @BeforeEach
    void setUp() {
        usuarioExistente = Usuario.builder()
                .id(1L)
                .nombre("Juan")
                .email("juan@donaton.cl")
                .password("$2a$10$hashedpassword")
                .rol(Rol.DONANTE)
                .fechaRegistro(LocalDateTime.now())
                .build();
    }

    @Test
    void loginExitoso() {
        LoginRequest request = new LoginRequest();
        request.setEmail("juan@donaton.cl");
        request.setPassword("123456");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(usuarioRepository.findByEmail("juan@donaton.cl"))
                .thenReturn(Optional.of(usuarioExistente));
        when(jwtService.generateToken(any(Map.class), eq(usuarioExistente)))
                .thenReturn("token.jwt.valido");

        AuthResponse response = authService.login(request);

        assertThat(response.getToken()).isEqualTo("token.jwt.valido");
        assertThat(response.getEmail()).isEqualTo("juan@donaton.cl");
        assertThat(response.getRol()).isEqualTo(Rol.DONANTE);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void loginFallido() {
        LoginRequest request = new LoginRequest();
        request.setEmail("juan@donaton.cl");
        request.setPassword("passwordIncorrecta");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Credenciales incorrectas"));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);

        verify(usuarioRepository, never()).findByEmail(anyString());
    }

    @Test
    void registroExitoso() {
        RegisterRequest request = new RegisterRequest();
        request.setNombre("Maria");
        request.setEmail("maria@donaton.cl");
        request.setPassword("abcdef");
        request.setRol(Rol.DONANTE);

        when(passwordEncoder.encode("abcdef")).thenReturn("$2a$10$hashedmaria");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));
        when(jwtService.generateToken(any(Map.class), any(Usuario.class)))
                .thenReturn("token.nuevo.usuario");

        AuthResponse response = authService.register(request);

        assertThat(response.getToken()).isEqualTo("token.nuevo.usuario");
        assertThat(response.getEmail()).isEqualTo("maria@donaton.cl");
        assertThat(response.getRol()).isEqualTo(Rol.DONANTE);
        verify(usuarioRepository).save(any(Usuario.class));
        verify(passwordEncoder).encode("abcdef");
    }

    @Test
    void registroDuplicado() {
        RegisterRequest request = new RegisterRequest();
        request.setNombre("Juan");
        request.setEmail("juan@donaton.cl");
        request.setPassword("123456");
        request.setRol(Rol.DONANTE);

        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashed");
        when(usuarioRepository.save(any(Usuario.class)))
                .thenThrow(new org.springframework.dao.DataIntegrityViolationException("Email duplicado"));

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(org.springframework.dao.DataIntegrityViolationException.class);
    }
}
