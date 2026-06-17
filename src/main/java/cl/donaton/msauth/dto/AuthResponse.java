package cl.donaton.msauth.dto;

import cl.donaton.msauth.entity.Rol;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String email;
    private Rol rol;
    private String nombre;
}
