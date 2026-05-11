package cl.donaton.msauth.controller;

import cl.donaton.msauth.dto.UsuarioDto;
import cl.donaton.msauth.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
}
