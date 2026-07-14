package cl.donaton.msauth.controller;

import cl.donaton.msauth.dto.AuthResponse;
import cl.donaton.msauth.dto.LoginRequest;
import cl.donaton.msauth.dto.RegisterRequest;
import cl.donaton.msauth.entity.Rol;
import cl.donaton.msauth.filter.JwtAuthenticationFilter;
import cl.donaton.msauth.service.AuthService;
import cl.donaton.msauth.service.UsuarioDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// addFilters=false: OncePerRequestFilter.doFilter() es final y no se puede stubear con Mockito,
// por lo que deshabilitamos los filtros de servlet para que las requests lleguen al controller.
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private UsuarioDetailsService usuarioDetailsService;

    @Test
    void testLogin_validCredentials_returns200WithToken() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@test.com");
        request.setPassword("password123");

        AuthResponse response = new AuthResponse("mocked-token", "user@test.com", Rol.DONANTE, "Usuario Test");
        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked-token"))
                .andExpect(jsonPath("$.email").value("user@test.com"));
    }

    @Test
    void testLogin_wrongPassword_returns401() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@test.com");
        request.setPassword("wrong");

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new BadCredentialsException("Credenciales incorrectas"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testLogin_userNotFound_returns401() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("noexiste@test.com");
        request.setPassword("anypassword");

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new BadCredentialsException("Usuario no encontrado"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    // El controller usa ResponseEntity.ok() → retorna 200, no 201.
    // Para retornar 201 habría que cambiar a ResponseEntity.status(HttpStatus.CREATED).body(...).
    @Test
    void testRegister_newUser_returns200() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setNombre("Benja");
        request.setEmail("benja@test.com");
        request.setPassword("pass123");
        request.setRut("12345678-9");

        AuthResponse response = new AuthResponse("mocked-token", "benja@test.com", Rol.DONANTE, "Benja");
        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked-token"))
                .andExpect(jsonPath("$.email").value("benja@test.com"));
    }

    @Test
    void testRegister_duplicateEmail_returns409() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setNombre("Benja");
        request.setEmail("duplicate@test.com");
        request.setPassword("pass123");
        request.setRut("12345678-9");

        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new DataIntegrityViolationException("Email duplicado"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }
}
