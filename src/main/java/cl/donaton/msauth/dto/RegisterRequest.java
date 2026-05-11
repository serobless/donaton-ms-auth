package cl.donaton.msauth.dto;

import cl.donaton.msauth.entity.Rol;
import lombok.Data;

@Data
public class RegisterRequest {
    private String nombre;
    private String email;
    private String password;
    private Rol rol;
}
