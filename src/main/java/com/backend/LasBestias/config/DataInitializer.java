package com.backend.LasBestias.config;

import com.backend.LasBestias.model.Usuario;
import com.backend.LasBestias.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {

        if (adminEmail == null || adminPassword == null) {
            System.out.println("⚠️ Variables de entorno del ADMIN no configuradas");
            return;
        }

        if (usuarioRepository.findByEmail(adminEmail).isEmpty()) {

            Usuario admin = new Usuario();
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRol("ADMIN");

            usuarioRepository.save(admin);

            System.out.println("✅ ADMIN creado automáticamente");
        } else {
            System.out.println("ℹ️ ADMIN ya existe");
        }
    }
}