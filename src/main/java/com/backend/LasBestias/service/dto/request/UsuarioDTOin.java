package com.backend.LasBestias.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDTOin {
    private String email;
    private String password;
    private String rol; // "ADMIN" o "USER"
}
