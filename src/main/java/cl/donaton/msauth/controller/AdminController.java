package cl.donaton.msauth.controller;

import cl.donaton.msauth.dto.UsuarioDto;
import cl.donaton.msauth.entity.Rol;
import cl.donaton.msauth.entity.Usuario;
import cl.donaton.msauth.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UsuarioRepository usuarioRepository;

    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioDto>> listarUsuarios() {
        List<UsuarioDto> usuarios = usuarioRepository.findAll()
                .stream()
                .map(UsuarioDto::fromUsuario)
                .collect(Collectors.toList());
        return ResponseEntity.ok(usuarios);
    }

    @PatchMapping("/usuarios/{id}/rol")
    public ResponseEntity<UsuarioDto> cambiarRol(@PathVariable Long id,
                                                  @RequestBody Map<String, String> body) {
        Rol nuevoRol;
        try {
            nuevoRol = Rol.valueOf(body.get("rol").toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rol inválido: " + body.get("rol"));
        }
        Usuario u = usuarioRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        u.setRol(nuevoRol);
        return ResponseEntity.ok(UsuarioDto.fromUsuario(usuarioRepository.save(u)));
    }
}
