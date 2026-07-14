package cl.donaton.msauth.config;

import cl.donaton.msauth.entity.Rol;
import cl.donaton.msauth.entity.Usuario;
import cl.donaton.msauth.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            if (usuarioRepository.findByEmail("admin@donaton.cl").isEmpty()) {
                usuarioRepository.save(Usuario.builder()
                        .nombre("Administrador")
                        .email("admin@donaton.cl")
                        .password(passwordEncoder.encode("123456"))
                        .rol(Rol.ADMIN)
                        .fechaRegistro(LocalDateTime.now())
                        .build());
                System.out.println(">> Usuario admin creado: admin@donaton.cl");
            }
            if (usuarioRepository.findByEmail("juan@donaton.cl").isEmpty()) {
                usuarioRepository.save(Usuario.builder()
                        .nombre("Juan")
                        .email("juan@donaton.cl")
                        .password(passwordEncoder.encode("123456"))
                        .rol(Rol.DONANTE)
                        .fechaRegistro(LocalDateTime.now())
                        .build());
                System.out.println(">> Usuario donante creado: juan@donaton.cl");
            }

            // centroId = orden de creación en ms-donaciones (Quilicura=1, Santiago=2, Valparaíso=3, Concepción=4)
            crearEncargado("Encargado Quilicura",    "encargado.quilicura@donaton.cl",  1L);
            crearEncargado("Encargado Santiago",     "encargado.santiago@donaton.cl",   2L);
            crearEncargado("Encargado Valparaíso",   "encargado.valparaiso@donaton.cl", 3L);
            crearEncargado("Encargado Concepción",   "encargado.concepcion@donaton.cl", 4L);
        };
    }

    private void crearEncargado(String nombre, String email, Long centroId) {
        usuarioRepository.findByEmail(email).ifPresentOrElse(u -> {
            if (u.getCentroId() == null) {
                u.setCentroId(centroId);
                usuarioRepository.save(u);
                System.out.println(">> CentroId asignado a: " + email + " → " + centroId);
            }
        }, () -> {
            usuarioRepository.save(Usuario.builder()
                    .nombre(nombre)
                    .email(email)
                    .password(passwordEncoder.encode("123456"))
                    .rol(Rol.CENTRO_ADMIN)
                    .centroId(centroId)
                    .fechaRegistro(LocalDateTime.now())
                    .build());
            System.out.println(">> Encargado creado: " + email + " → centroId=" + centroId);
        });
    }
}
