package com.backend.LasBestias.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(indexes = {
        @Index(name = "type_index", columnList = "type"),
        @Index(name = "model_id_index", columnList = "modelId"),
        @Index(name = "user_id_index", columnList = "userId"),
})
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalName;

    @Column(nullable = false)
    private UUID s3name;

    private long size;

    private String extension;

    @Enumerated(EnumType.STRING)
    private ImageType type;

    private LocalDateTime createdAt;

    private Long modelId;

    private Long userId;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
