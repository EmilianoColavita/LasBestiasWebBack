package com.backend.LasBestias.service;


import com.backend.LasBestias.model.Usuario;
import com.backend.LasBestias.service.dto.request.UsuarioDTOin;
import com.backend.LasBestias.service.dto.response.UsuarioDTO;

public interface UsuarioService {
    UsuarioDTO registrarUsuario(UsuarioDTOin dto);
    Usuario buscarPorEmail(String email);
}

