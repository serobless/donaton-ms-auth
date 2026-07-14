package cl.donaton.msauth.service;

import cl.donaton.msauth.entity.Rol;
import cl.donaton.msauth.entity.Usuario;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    // Clave Base64 de 256 bits válida para HMAC-SHA256
    private static final String SECRET =
            "dGVzdFNlY3JldEtleUZvckpXVFRlc3RpbmdQdXJwb3NlczEyMzQ1Ng==";

    private JwtService jwtService;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", SECRET);
        ReflectionTestUtils.setField(jwtService, "expiration", 3600000L); // 1 hora

        usuario = Usuario.builder()
                .id(1L)
                .nombre("Juan")
                .email("juan@donaton.cl")
                .password("$2a$10$hashed")
                .rol(Rol.DONANTE)
                .fechaRegistro(LocalDateTime.now())
                .build();
    }

    @Test
    void generarTokenValido() {
        String token = jwtService.generateToken(Map.of("rol", Rol.DONANTE), usuario);

        assertThat(token).isNotBlank();

        String emailExtraido = jwtService.extractUsername(token);
        assertThat(emailExtraido).isEqualTo("juan@donaton.cl");

        // Verifica que el claim "rol" está presente
        Object rol = jwtService.extractClaim(token, claims -> claims.get("rol"));
        assertThat(rol).isNotNull().hasToString("DONANTE");
    }

    @Test
    void validarTokenValido() {
        String token = jwtService.generateToken(Map.of("rol", Rol.DONANTE), usuario);

        boolean esValido = jwtService.isTokenValid(token, usuario);

        assertThat(esValido).isTrue();
    }

    @Test
    void validarTokenExpirado() {
        // Genera un token que ya expiró (expiration = -1000 ms)
        ReflectionTestUtils.setField(jwtService, "expiration", -1000L);
        String tokenExpirado = jwtService.generateToken(Map.of("rol", Rol.DONANTE), usuario);

        assertThatThrownBy(() -> jwtService.isTokenValid(tokenExpirado, usuario))
                .isInstanceOf(ExpiredJwtException.class);
    }
}
