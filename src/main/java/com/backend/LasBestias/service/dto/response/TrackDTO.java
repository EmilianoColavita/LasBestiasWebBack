package com.backend.LasBestias.service.dto.response;

public class TrackDTO {

    private String id;
    private String titulo;
    private String imagenUrl;
    private String spotifyId;
    private String urlSpotify;
    private String tipo;

    public TrackDTO() {}

    public TrackDTO(String id, String titulo, String imagenUrl, String spotifyId, String urlSpotify, String tipo) {
        this.id = id;
        this.titulo = titulo;
        this.imagenUrl = imagenUrl;
        this.spotifyId = spotifyId;
        this.urlSpotify = urlSpotify;
        this.tipo = tipo;
    }

    public String getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public String getSpotifyId() {
        return spotifyId;
    }

    public String getUrlSpotify() {
        return urlSpotify;
    }

    public String getTipo() {
        return tipo;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public void setSpotifyId(String spotifyId) {
        this.spotifyId = spotifyId;
    }

    public void setUrlSpotify(String urlSpotify) {
        this.urlSpotify = urlSpotify;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}