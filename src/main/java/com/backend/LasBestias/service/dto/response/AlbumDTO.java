package com.backend.LasBestias.service.dto.response;

import java.util.List;

public class AlbumDTO {

    private String id;
    private String nombre;
    private String imagen;
    private String fecha;
    private List<TrackDTO> tracks;

    public AlbumDTO(){}

    public AlbumDTO(String id, String nombre, String imagen, String fecha, List<TrackDTO> tracks) {
        this.id = id;
        this.nombre = nombre;
        this.imagen = imagen;
        this.fecha = fecha;
        this.tracks = tracks;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getImagen() { return imagen; }
    public String getFecha() { return fecha; }
    public List<TrackDTO> getTracks() { return tracks; }

    public void setId(String id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setImagen(String imagen) { this.imagen = imagen; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public void setTracks(List<TrackDTO> tracks) { this.tracks = tracks; }
}