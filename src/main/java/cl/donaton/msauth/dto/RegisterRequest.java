package cl.donaton.msauth.dto;

import cl.donaton.msauth.entity.Rol;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {
    private String nombre;
    private String email;
    private String password;
    private Rol rol;

    @NotBlank
    private String rut;

    private String telefono;
    private String region;
}
