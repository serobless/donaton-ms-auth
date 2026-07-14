package cl.donaton.msauth.dto;

import cl.donaton.msauth.entity.Rol;
import cl.donaton.msauth.entity.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UsuarioDto {
    private Long id;
    private String nombre;
    private String email;
    private Rol rol;
    private LocalDateTime fechaRegistro;
    private Long centroId;

    public static UsuarioDto fromUsuario(Usuario u) {
        return new UsuarioDto(u.getId(), u.getNombre(), u.getEmail(), u.getRol(), u.getFechaRegistro(), u.getCentroId());
    }
}
