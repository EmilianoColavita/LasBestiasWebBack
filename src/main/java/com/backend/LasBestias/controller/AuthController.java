package com.backend.LasBestias.controller;

import com.backend.LasBestias.model.Usuario;
import com.backend.LasBestias.repository.UsuarioRepository;
import com.backend.LasBestias.service.UsuarioService;
import com.backend.LasBestias.service.dto.request.AuthRequest;
import com.backend.LasBestias.service.dto.request.UsuarioDTOin;
import com.backend.LasBestias.service.dto.response.AuthResponse;
import com.backend.LasBestias.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;

    @Operation(summary = "Registrar nuevo usuario", tags = "Autenticación")
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UsuarioDTOin dto) {
        usuarioService.registrarUsuario(dto);
        return ResponseEntity.ok("Usuario registrado");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        Usuario usuario = usuarioService.buscarPorEmail(request.getEmail());

        String token = jwtUtil.generateToken(usuario.getEmail(), usuario.getRol());
        return ResponseEntity.ok(new AuthResponse(token));
    }
}


