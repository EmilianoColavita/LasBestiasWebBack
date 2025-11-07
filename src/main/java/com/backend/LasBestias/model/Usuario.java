package com.backend.LasBestias.model;

import jakarta.persistence.*;
import lombok.Data;



@Entity
@Data
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 100, nullable = false)
    private String email;

    private String password;

    private String rol;


}
